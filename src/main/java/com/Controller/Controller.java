package com.Controller;

import com.entity.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

@RestController
@RequestMapping(path = "/rooms")
public class Controller {

    @Autowired
    public EntityManager em;

    @GetMapping(path="/", produces = "application/json")
    public List<Room> getEmployees()
    {
//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("/Users/stsai/Desktop/points010.odb");
//        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        // Retrieve all the Cook objects from the database: (Sth needs to be fixed for query many-to-one relation data
        TypedQuery<Room> query = em.createQuery("SELECT r FROM Room r", Room.class);
        List<Room> results = query.getResultList();
        for (Room r : results) {
            System.out.println(r);
        }

        em.getTransaction().commit();


        return results;
    }

}
