package com.example.Banter;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Erlend on 17.10.2014.
 */
public class BanterPost implements Serializable {

    private int id;
    private String name;
    private String text;
    private SerializableBitmap image;
    private String imagePath;
    private String time;
    private int likes;
    private boolean liked;

    public BanterPost() {
        liked = false;
    }

    /**
     * Sets the current objects time to the time when this method is called.
     */
    public void setTimeAndDateToNow() {

        /* retrieve current time and date */
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date now = new Date();
        time = sdfDate.format(now);
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public void incrementLikes() {
        likes++;
    }

    public void decrementLikes() {
        likes--;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Bitmap getImage() {
        if (image != null)
            return image.picture;
        else
            return null;
    }

    public void setImage(Bitmap image) {
        this.image = new SerializableBitmap(image);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
