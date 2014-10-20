package com.example.Banter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.*;

/**
 * Created by jacobmeidell on 06.10.14.
 *
 * Wrapper to be able to persist bitmap images
 */
public class SerializableBitmap implements Serializable {

    Bitmap picture;

    public SerializableBitmap(Bitmap picture) {

        this.picture = picture;
    }

    public Bitmap getBitmap() {
        return picture;
    }

    /* Write the bitmap image as a stream of bytes */
    private void writeObject(ObjectOutputStream out) throws IOException {
        if (picture != null) {
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            picture.compress(Bitmap.CompressFormat.PNG, 100, stream);
            final byte[] imageByteArray = stream.toByteArray();
            out.writeInt(imageByteArray.length);
            out.write(imageByteArray);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{

        final int length = in.readInt();

        if (length != -1) {
            final byte[] imageByteArray = new byte[length];
            in.readFully(imageByteArray);
            picture = BitmapFactory.decodeByteArray(imageByteArray, 0, length);
        }
    }
}

