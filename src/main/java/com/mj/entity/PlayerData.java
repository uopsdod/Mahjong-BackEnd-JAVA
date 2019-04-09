package com.mj.entity;

import lombok.Data;

@Data
public class PlayerData {
    private Long playerID;
    private Long roomID;
    public PlayerData(Long playerID, Long roomID){
        this.playerID = playerID;
        this.roomID = roomID;
    }
}
