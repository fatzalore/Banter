package com.example.Banter;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jacobmeidell on 19.10.14.
 */
public class BanterDataModel implements Serializable {

    static public ArrayList<BanterRoom> banterRooms;


    public BanterDataModel(){
        banterRooms = new ArrayList<BanterRoom>();
    }

    public static ArrayList<BanterRoom> getBanterRooms() {
        return banterRooms;
    }

    public static void addBanterRoom(BanterRoom banterRoom){
        banterRooms.add(banterRoom);
    }
}
