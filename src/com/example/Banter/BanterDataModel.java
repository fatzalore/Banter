package com.example.Banter;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jacobmeidell on 19.10.14.
 */
public class BanterDataModel implements Serializable {

    public ArrayList<BanterRoom> banterRooms;
    public BanterRoom currentRoom; // current room is what is currently polled info from and displayed in roomFragment

    public int imageCounter;

    public BanterDataModel(){
        banterRooms = new ArrayList<BanterRoom>();
        imageCounter = 1;
    }

    public ArrayList<BanterRoom> getBanterRooms() {
        return banterRooms;
    }

    public boolean isBanterRoomInList(BanterRoom banterRoom){
        for(BanterRoom b : banterRooms){
            if(banterRoom.equals(b)){
                return true;
            }
        }
        return false;
    }

    public void addBanterRoom(BanterRoom banterRoom){
        banterRooms.add(banterRoom);
    }
}
