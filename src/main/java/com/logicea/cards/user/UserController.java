package com.logicea.cards.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cards")

public class UserController {
    @Autowired
    private UserRepository userRepository;
    UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    //εδω λογικα θα βάλω τις μεθόδους get post patch put delete
    //αλλα μέσα σε αυτες πρεπει να γίνεται πρώτα το autherization
    //για να μπορώ να πάρω το ρόλο του μετα τα διατρέξω τις κάρτες
    //και να ου επιστρεψω τις σωστες και ετα να κάνω τη αλλαγή.

}
