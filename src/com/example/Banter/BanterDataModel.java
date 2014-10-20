package com.example.Banter;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jacobmeidell on 19.10.14.
 */
public class BanterDataModel implements Serializable {

    public ArrayList<BanterRoom> banterRooms;


    public BanterDataModel(){
        banterRooms = new ArrayList<BanterRoom>();
    }

    public ArrayList<BanterRoom> getBanterRooms() {
        return banterRooms;
    }

    public void addBanterRoom(BanterRoom banterRoom){
        banterRooms.add(banterRoom);
    }
}
