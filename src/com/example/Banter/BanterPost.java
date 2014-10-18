package com.example.Banter;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Erlend on 17.10.2014.
 */
public class BanterPost {

    private String name;
    private String text;
    private Bitmap image;
    private String time;
    private int likes;

    public BanterPost() {

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
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
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
