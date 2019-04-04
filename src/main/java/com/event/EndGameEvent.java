package com.event;

import com.entity.Player;
import com.entity.Room;
import com.google.common.collect.BiMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;

public class EndGameEvent implements Event {

    public static final String EVENT_SUFFIX = "_done";
    public static final String EVENT_ENDGAME = "endgame";
    private final WebSocketSession session;
    private final JsonObject payloadJsonObj;
    private final BiMap<WebSocketSession, Player> playerMaps;
    private final BiMap<WebSocketSession, Room> roomMaps;

    public EndGameEvent(WebSocketSession session, JsonObject payloadJsonObj, BiMap<WebSocketSession, Player> playerMaps, BiMap<WebSocketSession, Room> roomMaps){
        this.session = session;
        this.payloadJsonObj = payloadJsonObj;
        this.playerMaps = playerMaps;
        this.roomMaps = roomMaps;
    }

    @Override
    public void execute() {
        try {
            System.out.println("endgame matched");
            Gson gson = new Gson();
            JsonObject jobj = new JsonObject();

            Room room = roomMaps.get(session);
            List<Player> playersToLeaveRoom = room.getPlayers();
            for (Player player: playersToLeaveRoom){

                BiMap<Player, WebSocketSession> inverse = playerMaps.inverse();
                WebSocketSession sessionInRoom =inverse.get(player);

                roomMaps.remove(sessionInRoom);

                jobj.addProperty("event", EVENT_ENDGAME + EVENT_SUFFIX);

                    sessionInRoom.sendMessage(new TextMessage(gson.toJson(jobj)));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
