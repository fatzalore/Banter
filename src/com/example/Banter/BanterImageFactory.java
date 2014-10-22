package com.example.Banter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Erlend on 22.10.2014.
 */
public class BanterImageFactory {

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight)
    { // BEST QUALITY MATCH

        //First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight)
        {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth)
        {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    /* This method cannot be run on main thread -> NetworkOnMainThreadException */
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public static String getEncodedStringFromBitmap(Bitmap bitmap) {
        // Convert bitmap to byte[]
        ByteArrayOutputStream output = new ByteArrayOutputStream(bitmap.getByteCount());
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        byte[] imageBytes = output.toByteArray();
        // Convert byte[] to string
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

    public static Bitmap getDecodedBitmapFromString(String string) {
        // Convert string to byte[]
        byte[] newImageBytes = Base64.decode(string, Base64.NO_WRAP);
        // Convert byte[] back to bitmap
        return BitmapFactory.decodeByteArray(newImageBytes, 0, newImageBytes.length);
    }

    public static void sendImageToServer(Bitmap bitmap, String filePath) {
        // Send image data to server
        ByteArrayOutputStream byteStream = null;

        try {
            // Create HTTP objects
            DefaultHttpClient httpClient = new DefaultHttpClient();
            BasicHttpParams hparams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(hparams, 10 * 1000);
            httpClient.setParams(hparams);
            HttpPost httpPost = new HttpPost("http://vie.nu/banter/banterImages/addImage.php?filePath=" + filePath);

            byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteStream);

            // Convert ByteArrayOutputStream to byte array. Close stream.
            byte[] byteArray = byteStream.toByteArray();
            byteStream.close();
            byteStream = null;

            // Send HTTP Post request
            httpPost.setEntity(new ByteArrayEntity(byteArray));
            String response = EntityUtils.toString(httpClient.execute(httpPost).getEntity(), HTTP.UTF_8);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (byteStream != null) byteStream.close();
            } catch (Exception e) {}
        }
    }
}
