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
    private boolean updateChecked;
    private String timeSinceLastUpdated;

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
    public boolean isUpdateChecked() {
        return updateChecked;
    }
    public void setUpdateChecked(boolean updateChecked) {
        this.updateChecked = updateChecked;
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

    public String getTimeSinceLastUpdated() {
        return timeSinceLastUpdated;
    }

    public void setTimeSinceLastUpdated() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date now = new Date();
        String nowString = sdfDate.format(now);

        /* TODO: find months/days since last post to */

        /* finding hours/minutes since last post */
        int nowHours = Integer.parseInt(nowString.substring(11, 12));
        int nowMinutes = Integer.parseInt(nowString.substring(14, 15));
        int lastHours = Integer.parseInt(lastUpdated.substring(11, 12));
        int lastMinutes = Integer.parseInt(lastUpdated.substring(14, 15));
        int minutes;
        int hours;
        if (nowHours > lastHours) {
            hours = nowHours - lastHours;
        } else if (nowHours < lastHours){
            hours = 24 - lastHours + nowHours;
        } else {
            hours = 0;
        }
        if (nowMinutes > lastMinutes) {
            minutes = nowMinutes - lastMinutes;
        } else if (nowMinutes < lastMinutes) {
            minutes = 60 - lastMinutes + nowMinutes;
            hours--;
        } else {
            minutes = 0;
        }
        timeSinceLastUpdated = "";
        if (hours > 0) {
            timeSinceLastUpdated = hours + " days ago";
        }
        else if (minutes > 0) {
            timeSinceLastUpdated = minutes + " minutes ago";
        } else {
            timeSinceLastUpdated = "just now";
        }
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

    public boolean equals(BanterRoom other){
        if(this.getId() == other.getId()){
            return true;
        }
        return false;
    }

    public BanterPost getPost(int id) {
        for (BanterPost b : posts) {
            if (b.getId() == id) {
                return b;
            }
        }
        return null;
    }

    public String getTimeSinceLastUpdate() {
        return timeSinceLastUpdated;
    }
}
