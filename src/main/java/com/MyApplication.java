package com;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mj.AnnotationExclusionStrategy;
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("/Users/stsai/Desktop/points017.odb");
        return emf;
    }

    @Bean
    public EntityManager entityManager(){
        EntityManager em = entityManagerFactory().createEntityManager();
        return em;
    }

    @Bean
    public Gson gson(){
        Gson gson = new GsonBuilder().setExclusionStrategies(new AnnotationExclusionStrategy()).create();
        return gson;
    }



    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }

}