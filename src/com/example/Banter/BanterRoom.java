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
    private int amount;
    private String name;
    private String dateCreated;
    private String lastUpdated;
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
        posts = new ArrayList<BanterPost>();
        amount = 0;
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
        amount++;
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
    public void setPostAmount(int posts){
        this.amount=posts;
    }
    public int getPostAmount(){
        return amount;
    }
}
