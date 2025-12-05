/*package com.logicea.cards;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

// new main class for checking connection sql->spring
@SpringBootApplication
public class DemoApplicationConnection implements CommandLineRunner {


    @Autowired // inject to my repository
    private CardRepository cardRepository;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplicationConnection.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("--- CHECK CONNECTION SQL -> SPRING ---");
        long cardCount = cardRepository.count(); // run internal SQL: SELECT COUNT(*) FROM cards;
        System.out.println("Found" + cardCount + " cards");
        System.out.println("--------------------------------------");
    }
}*/
