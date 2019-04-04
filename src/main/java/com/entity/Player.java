package com.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import javax.persistence.Entity;

@Data
//@AllArgsConstructor
@Entity
public class Player {
    private String playerID;
    private String status;

    public Player(String playerID, String status){
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
