package com.mj.event;

import com.mj.entity.Player;
import com.mj.entity.Room;
import com.google.common.collect.BiMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.persistence.EntityManager;

@Component
public class EventFactory {

    @Autowired
    public EntityManager em;

    public Event execute(WebSocketSession session, String payload, BiMap<WebSocketSession, Player> playerMaps, BiMap<WebSocketSession, Room> roomMaps){
        try{
            /** verify if payload is a jsonObject **/
            JsonElement jsonElement = new JsonParser().parse(payload);
            if (!jsonElement.isJsonObject()){
                return null;
            }

            JsonObject payloadJsonObj = jsonElement.getAsJsonObject();
            String event = payloadJsonObj.get("event").getAsString();

            /** create event **/
            if (event.equalsIgnoreCase("joingame")) {
                return new JoinGameEvent(session, payloadJsonObj, playerMaps, roomMaps, em);
            }

            if (event.equalsIgnoreCase("endgame")){
                return  new EndGameEvent(session, payloadJsonObj, playerMaps, roomMaps, em);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
