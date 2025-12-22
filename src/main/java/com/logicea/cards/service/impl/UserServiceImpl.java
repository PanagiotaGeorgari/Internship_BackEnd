package com.logicea.cards.service.impl;

import com.logicea.cards.dto.UserDto;
import com.logicea.cards.entity.User;
import com.logicea.cards.mapper.UserMapper;
import com.logicea.cards.repository.UserRepository;
import com.logicea.cards.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public UserInfoServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        System.out.println(user.getPassword());
        userRepository.save(user);
        return UserMapper.toDto(user);
    }
}
