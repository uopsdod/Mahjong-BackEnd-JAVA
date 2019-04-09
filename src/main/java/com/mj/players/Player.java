package com.mj.players;

import com.mj.Exclude;
import com.mj.room.Room;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

@Data
//@AllArgsConstructor
@Entity
public class Player {
    @Id
    private Long playerID;
    private String status;
    @Column(name="ROOM_ID")
    private Long roomID;

    @Exclude
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="ROOM_ID")
    private Room room;

    public Player(Long playerID, String status){
        this.playerID = playerID;
        this.status = status;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if ((o instanceof Player)
                && (((Player) o).getPlayerID().equals(this.playerID))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return playerID.hashCode();
    }

}
