package com.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Room {
    @Id private String roomID;
    private Long startMS;
    private Long endMS;

//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy="room")  // for error: Attempt to persist a reference to a non managed com.entity.Player instance - field com.entity.Room.players // ref: https://stackoverflow.com/questions/15749359/objectdb-relation-ships
    @OneToMany(targetEntity=com.entity.Player.class, cascade=CascadeType.ALL, mappedBy="room")
    private Set<Player> players = new HashSet<>();

    public Room(String roomID){
        this.roomID = roomID;
        this.startMS = new java.util.Date().getTime();
    }
}
