package com.example.Banter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Erlend on 17.10.2014.
 */
public class BanterRoom {

    private int id;
    private String name;
    private String dateCreated;
    private String lastUpdated;
    private String likes;
    private ArrayList<BanterPost> posts;

    public BanterRoom(String name) {
        this.name = name;

        /* retrieve current time and date */
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date now = new Date();
        dateCreated = sdfDate.format(now);
        lastUpdated = dateCreated;
        likes = Integer.toString(15);
        posts = new ArrayList<BanterPost>();
    }

    public void addPost(BanterPost post) {
        posts.add(post);
    }

    public ArrayList<BanterPost> getPosts() {
        return posts;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastUpdated(Date updated){
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        lastUpdated = sdfDate.format(updated);
    }
    public String getLastUpdated(){
        return lastUpdated;
    }
    public void setLikes(int likes){
        this.likes = Integer.toString(likes);
    }
    public String getLikes(){
        return likes;
    }
}
