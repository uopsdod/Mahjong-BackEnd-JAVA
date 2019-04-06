package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@Configuration
@SpringBootApplication
@EnableWebSocket
public class MyApplication {

    @Bean
    public EntityManagerFactory entityManagerFactory(){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("/Users/stsai/Desktop/points010.odb");
        return emf;
    }

    @Bean
    public EntityManager entityManager(){
        EntityManager em = entityManagerFactory().createEntityManager();
        return em;
    }

    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }

}