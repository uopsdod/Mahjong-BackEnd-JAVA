package com.mj.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class RoomData {
    private Long roomID;
    private Set<PlayerData> players = new HashSet<>();;
    public RoomData(Long roomID, Player _player){
        this.roomID = roomID;
        PlayerData pd = new PlayerData(_player.getPlayerID(), roomID);
        players.add(pd);
//
//        for (Player p : _players) {
//            PlayerData pd = new PlayerData(p.getPlayerID(), roomID);
//            players.add(pd);
//        }
    }
}
