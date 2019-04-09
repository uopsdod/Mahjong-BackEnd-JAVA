package com.mj.event;

import com.google.gson.GsonBuilder;
import com.mj.AnnotationExclusionStrategy;
import com.mj.players.Player;
import com.mj.room.Room;
import com.google.common.collect.BiMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.jdo.annotations.Transactional;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class JoinGameEvent implements Event {

    public static final String EVENT_SUFFIX = "_done";
    public static final String EVENT_JOINGAME = "joingame";
    public static final int expectedNumberOfPlayer = 2;
    private final WebSocketSession session;
    private final JsonObject payloadJsonObj;
    private final BiMap<WebSocketSession, Player> playerMaps;
    private final BiMap<WebSocketSession, Room> roomMaps;
    private final EntityManager em;

    public JoinGameEvent(WebSocketSession session, JsonObject payloadJsonObj, BiMap<WebSocketSession, Player> playerMaps, BiMap<WebSocketSession, Room> roomMaps, EntityManager em){
        this.session = session;
        this.payloadJsonObj = payloadJsonObj;
        this.playerMaps = playerMaps;
        this.roomMaps = roomMaps;
        this.em = em;
    }

    @Override
    public void execute() {
        try {
            this.joinGame(session, payloadJsonObj, playerMaps, roomMaps);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    private void joinGame(WebSocketSession session, JsonObject jsonObject, BiMap<WebSocketSession, Player> playerMaps, BiMap<WebSocketSession, Room> roomMaps) throws IOException {
        Gson gson = new Gson();
        JsonObject jobj = new JsonObject();
        Long playerId = jsonObject.get("playerId").getAsLong();
        System.out.println("joingame matched");
        Player p = new Player(playerId, "waitting");
        playerMaps.put(session, p);
        System.out.println("players ++: " + playerMaps.values().toString());

//         insert player into db if no exist
//        em.persist(p);

        // send back ack to clients
        jobj.addProperty("event", EVENT_JOINGAME + EVENT_SUFFIX);
        jobj.add("player", gson.toJsonTree(p));
        session.sendMessage(new TextMessage(gson.toJson(jobj)));

        // check current waiting number
        long waitingNumber = playerMaps.values().stream().filter(innerPlayer -> innerPlayer.getStatus().equalsIgnoreCase("waitting")).count();
        System.out.println("waitingNumber: " + waitingNumber);
        if (waitingNumber >= expectedNumberOfPlayer) {
            initiateGame(playerMaps, expectedNumberOfPlayer, roomMaps, session);
        }
    }
    private void initiateGame(BiMap<WebSocketSession, Player> playerMaps, int expectedNumberOfPlayer, BiMap<WebSocketSession, Room> roomMaps
            ,WebSocketSession session) throws IOException {
        JsonObject jobj = new JsonObject();
        Gson gson = new GsonBuilder().setExclusionStrategies(new AnnotationExclusionStrategy()).create();

        // initiate a game
        System.out.println("enough waiting players - create a room");
        int count = 0;
        Room room = new Room(RandomUtils.nextLong(1000000,9999999));
        Set<Player> roomPlayers = room.getPlayers();

        // collect players to the same room
        for (Map.Entry<WebSocketSession, Player> entry : playerMaps.entrySet()){

            Player player = entry.getValue();
            player.setStatus("playing");
            player.setRoom(room); // many-to-one
            roomPlayers.add(player); // one-to-many

            count++;
            if (count >= expectedNumberOfPlayer) {
                break;
            }
        }
        System.out.println("room.getPlayers().size(): " + room.getPlayers().size());

        // send "initiateGame to all players in this room
        for (Player player: roomPlayers){

            roomMaps.put(session,room);

            BiMap<Player, WebSocketSession> inverse = playerMaps.inverse();
            WebSocketSession sessionInRoom =inverse.get(player);


            jobj.addProperty("event", "initiateGame");
            jobj.add("room", gson.toJsonTree(room));

            sessionInRoom.sendMessage(new TextMessage(gson.toJson(jobj)));
        }

    }
}
