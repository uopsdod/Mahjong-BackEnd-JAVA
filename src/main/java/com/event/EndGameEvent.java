package com.event;

import com.entity.Player;
import com.entity.Room;
import com.google.common.collect.BiMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;
import java.util.List;
import java.util.Set;

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
            Set<Player> playersToLeaveRoom = room.getPlayers();
            for (Player player: playersToLeaveRoom){

                BiMap<Player, WebSocketSession> inverse = playerMaps.inverse();
                WebSocketSession sessionInRoom =inverse.get(player);

                roomMaps.remove(sessionInRoom);

                jobj.addProperty("event", EVENT_ENDGAME + EVENT_SUFFIX);

                sessionInRoom.sendMessage(new TextMessage(gson.toJson(jobj)));

            }

            // TODO: store the result in db
            room.setEndMS(new java.util.Date().getTime());
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("/Users/stsai/Desktop/points009.odb");
            EntityManager em = emf.createEntityManager();

            em.getTransaction().begin();
            em.persist(room);
            em.getTransaction().commit();

            // Retrieve all the Cook objects from the database: (Sth needs to be fixed for query many-to-one relation data
//            TypedQuery<Room> query = em.createQuery("SELECT r FROM Room r", Room.class);
//            List<Room> results = query.getResultList();
//            for (Room r : results) {
//                System.out.println(r);
//            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
