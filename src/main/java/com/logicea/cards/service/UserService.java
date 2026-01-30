package com.logicea.cards.service;

import com.logicea.cards.entity.User;

public interface UserService {
    User createUser(User user);

    boolean validateUser(String email, String password);
}
