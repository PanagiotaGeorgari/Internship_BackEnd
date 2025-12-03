package com.logicea.cards;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.logicea.cards.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner { // Υλοποιούμε το interface

    // Χρησιμοποιούμε το Autowired για να "ενέση" (inject) το repository μας
    @Autowired
    private CardRepository cardRepository;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // ΕΔΩ ΓΙΝΕΤΑΙ Ο ΕΛΕΓΧΟΣ
        System.out.println("--- ΕΛΕΓΧΟΣ ΣΥΝΔΕΣΗΣ ΒΑΣΗΣ ΔΕΔΟΜΕΝΩΝ ---");
        long cardCount = cardRepository.count(); // Εκτελείται SQL: SELECT COUNT(*) FROM cards;
        System.out.println("Βρέθηκαν " + cardCount + " κάρτες στον πίνακα cards.");
        System.out.println("--- Ο ΕΛΕΓΧΟΣ ΟΛΟΚΛΗΡΩΘΗΚΕ ---");
    }
}
