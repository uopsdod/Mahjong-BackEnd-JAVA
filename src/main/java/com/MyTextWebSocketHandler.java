package com;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

public class MyTextWebSocketHandler extends TextWebSocketHandler {
    public static final int expectedNumberOfPlayer = 2;
    public static final String EVENT_SUFFIX = "_done";
    public static final String EVENT_JOINGAME = "joingame";
    public static final String EVENT_ENDGAME = "endgame";
//    private LinkedHashMap<WebSocketSession, Player> playerMaps = new LinkedHashMap<>();

    private BiMap<WebSocketSession, Player> playerMaps = Maps.synchronizedBiMap(HashBiMap.create());
    private BiMap<WebSocketSession, Room> roomMaps = Maps.synchronizedBiMap(HashBiMap.create());
    private Gson gson = new Gson();

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // TODO: when someone leaves the room, terminate the game for all others as well
        playerMaps.remove(session);
        System.out.println("players --: " + playerMaps.values().toString());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

    }

    private void initiateGame(BiMap<WebSocketSession, Player> playerMaps, int expectedNumberOfPlayer, BiMap<WebSocketSession, Room> roomMaps
                                ,WebSocketSession session) throws IOException {
        JsonObject jobj = new JsonObject();

        // initiate a game
        System.out.println("enough waiting players - create a room");
        int count = 0;
        Room room = new Room(UUID.randomUUID().toString());
        List<Player> playersToJoin = room.getPlayers();

        // collect players to the same room
        for (Map.Entry<WebSocketSession, Player> entry : playerMaps.entrySet()){
            Player player = entry.getValue();
            player.setStatus("playing");

            playersToJoin.add(player);

            count++;
            if (count >= expectedNumberOfPlayer) {
                break;
            }
        }
        System.out.println("room.getPlayers().size(): " + room.getPlayers().size());

        // send "initiateGame to all players in this room
        for (Player player: playersToJoin){

            roomMaps.put(session,room);

            BiMap<Player, WebSocketSession> inverse = playerMaps.inverse();
            WebSocketSession sessionInRoom =inverse.get(player);


            jobj.addProperty("event", "initiateGame");
            jobj.add("room", gson.toJsonTree(room));

            sessionInRoom.sendMessage(new TextMessage(gson.toJson(jobj)));
        }

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) {
        JsonObject jobj = new JsonObject();
        // A message has been received
        String payload = textMessage.getPayload();
        System.out.println("Message received: " + payload);
        try{
            JsonElement jsonElement = new JsonParser().parse(payload);
            if (jsonElement.isJsonObject()){
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                String event = jsonObject.get("event").getAsString();

                if (event.equalsIgnoreCase("joingame")) {
                    String playerId = jsonObject.get("playerId").getAsString();
                    System.out.println("joingame matched");
                    Player p = new Player(playerId, "waitting");
                    playerMaps.put(session, p);
                    System.out.println("players ++: " + playerMaps.values().toString());

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

                if (event.equalsIgnoreCase("endgame")){
                    System.out.println("endgame matched");

                    Room room = roomMaps.get(session);
                    List<Player> playersToLeaveRoom = room.getPlayers();
                    for (Player player: playersToLeaveRoom){

                        BiMap<Player, WebSocketSession> inverse = playerMaps.inverse();
                        WebSocketSession sessionInRoom =inverse.get(player);

                        roomMaps.remove(sessionInRoom);

                        jobj.addProperty("event", EVENT_ENDGAME + EVENT_SUFFIX);
                        sessionInRoom.sendMessage(new TextMessage(gson.toJson(jobj)));
                    }

                }

            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
