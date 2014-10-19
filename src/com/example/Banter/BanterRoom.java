package com.example.Banter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Erlend on 17.10.2014.
 */
public class BanterRoom implements Serializable {

    private int id;
    private String name;
    private String dateCreated;
    private String lastUpdated;
    private String likes;
    private ArrayList<BanterPost> posts;
    private String password;
    private String adminPassword;

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
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password=password;
    }
    public String getAdminassword(){
        return adminPassword;
    }
    public void setAdminpassword(String password){
        this.adminPassword = password;
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

    public void setLastUpdated(String date){
        lastUpdated = date;
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
