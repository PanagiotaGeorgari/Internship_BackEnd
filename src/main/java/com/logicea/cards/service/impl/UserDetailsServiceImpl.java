package com.logicea.cards.service.impl;

import com.logicea.cards.entity.User;
import com.logicea.cards.mapper.UserDetailsMapper;
import com.logicea.cards.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service

public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userInfo= userRepository.findByEmail(email);
        return userInfo.map(UserDetailsMapper::new)
                .orElseThrow(() -> new UsernameNotFoundException("User "+ email+" not found"));
    }

}
