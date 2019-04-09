package com.mj.room;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jdo.annotations.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@RestController
@RequestMapping(path = "/rooms")
public class RoomController {

    @Autowired
    public EntityManager em;

    @Transactional
    @GetMapping(path="/", produces = "application/json")
    public List<RoomData> getEmployees()
    {
//        em.getTransaction().begin();

//        TypedQuery<Room> query = em.createQuery("SELECT r FROM Room r", Room.class);
//        List<Room> results = query.getResultList();
//        for (Room r : results) {
//            System.out.println(r);
//        }

        String queryStr ="SELECT NEW com.mj.room.RoomData(r.roomID, r.players) "
                         + "FROM Room AS r"; // this will call the constructor multiple times according to the size of the players list
        TypedQuery<RoomData> query2 = em.createQuery(queryStr, RoomData.class);
        List<RoomData> results2 = query2.getResultList();
        for (RoomData r : results2) {
            System.out.println(r);
        }

//        em.getTransaction().commit();

        return results2;
    }

}
