package com.mj.room;

import com.mj.players.Player;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Room {
    @Id
    @Column(name="ROOM_ID")
    private Long roomID;
    private Long startMS;
    private Long endMS;


    // for error: Attempt to persist a reference to a non managed Player instance - field Room.players // ref: https://stackoverflow.com/questions/15749359/objectdb-relation-ships
    // great ref for OneToMany: https://en.wikibooks.org/wiki/Java_Persistence/OneToMany#Example_of_a_OneToMany_relationship_and_inverse_ManyToOne_annotations
    @OneToMany(targetEntity= Player.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy="room")
    private Set<Player> players = new HashSet<>();

    public Room(Long roomID){
        this.roomID = roomID;
        this.startMS = new java.util.Date().getTime();
    }
}
