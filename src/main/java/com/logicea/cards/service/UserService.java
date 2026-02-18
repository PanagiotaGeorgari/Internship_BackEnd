package com.logicea.cards.service;

import com.logicea.cards.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService {
    User createUser(User user);

    boolean validateUser(String email, String password);
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;
}
