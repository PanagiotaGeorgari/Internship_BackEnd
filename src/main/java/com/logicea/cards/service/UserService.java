package com.logicea.cards.service;

import com.logicea.cards.dto.UserDto;
import com.logicea.cards.entity.User;

public interface UserService {
    public User createUser(User user);
    public boolean validateUser(String email, String password);
}
