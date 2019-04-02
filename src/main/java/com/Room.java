package com;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Room {
    private String roomID;
    private List<Player> players = new ArrayList<Player>();

    public Room(String roomID){
        this.roomID = roomID;
    }
}
