package com.example.Banter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Erlend on 17.10.2014.
 */
public class BanterRoom {

    private int id;
    private String name;
    private String dateCreated;

    public BanterRoom(String name) {
        this.name = name;

        /* retrieve current time and date */
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date now = new Date();
        dateCreated = sdfDate.format(now);
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
}
