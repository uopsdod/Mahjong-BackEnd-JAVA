package com.mj.Controller;

import com.google.gson.Gson;
import com.mj.entity.Player;
import com.mj.entity.PlayerData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jdo.annotations.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@RestController
@RequestMapping(path = "/players")
public class PlayerController {

    @Autowired
    public EntityManager em;

    @Autowired
    public Gson gson;

    @Transactional
    @GetMapping(path="/", produces = "application/json")
    public List<PlayerData> getPlayers()
    {
//        em.getTransaction().begin();

//        TypedQuery<Player> query = em.createQuery("SELECT p FROM Player p", com.mj.entity.Player.class);
//        List<Player> results = query.getResultList();
//        for (Player p : results) {
//            System.out.println(p);
//        }


        String queryStr ="SELECT NEW com.mj.entity.PlayerData(p.playerID, p.room.roomID) "
                        + "FROM Player AS p";
        TypedQuery<PlayerData> query2 = em.createQuery(queryStr, PlayerData.class);
        List<PlayerData> results2 = query2.getResultList();
        for (PlayerData p : results2) {
            System.out.println(p);
        }
        // TODO: find a way to return the value without hitting stackoverflow - json loop stuff
//        String s = gson.toJson(results);
//        System.out.println();
        return results2;
    }

}
