package com;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;

public class MyTextWebSocketHandler extends TextWebSocketHandler {
    public static final int expectedNumberOfPlayer = 2;
    public static final String EVENT_SUFFIX = "_done";
    public static final String EVENT_JOINGAME = "joingame";
//    private LinkedHashMap<WebSocketSession, Player> playerMaps = new LinkedHashMap<>();
    private BiMap<WebSocketSession, Player> playerMaps = HashBiMap.create();

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // TODO: when someone leaves the room, terminate the game for all others as well
        playerMaps.remove(session);
        System.out.println("players --: " + playerMaps.values().toString());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Player p = new Player(UUID.randomUUID().toString(), "waitting");
        playerMaps.put(session, p);
        System.out.println("players ++: " + playerMaps.values().toString());

        // check current waiting number
        long waitingNumber = playerMaps.values().stream().filter(innerPlayer -> innerPlayer.getStatus().equalsIgnoreCase("waitting")).count();
        System.out.println("waitingNumber: " + waitingNumber);

        // send back ack to clients
        session.sendMessage(new TextMessage(EVENT_JOINGAME + EVENT_SUFFIX));

        if (waitingNumber >= expectedNumberOfPlayer){
            System.out.println("enough waiting players - create a room");
            int count = 0;
            Room room = new Room(UUID.randomUUID().toString());
            List<Player> playersToJoin = new ArrayList<>();

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
                BiMap<Player, WebSocketSession> inverse = playerMaps.inverse();
                WebSocketSession sessionInRoom =inverse.get(player);
                sessionInRoom.sendMessage(new TextMessage("initiateGame"));
            }

        }

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        // A message has been received
        System.out.println("Message received: " + textMessage.getPayload());
    }
}
