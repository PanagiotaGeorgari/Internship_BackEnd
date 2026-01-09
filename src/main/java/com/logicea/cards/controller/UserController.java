package com.logicea.cards.controller;
import com.logicea.cards.dto.UserDto;
import com.logicea.cards.dto.LogInDto;
import com.logicea.cards.entity.User;
import com.logicea.cards.mapper.UserMapper;
import com.logicea.cards.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user-info")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> createUserInfo(@RequestBody UserDto userDto) {
        User user = UserMapper.toEntity(userDto);
        User newUser = userService.createUser(user);
        UserDto responseDto = UserMapper.toDto(newUser);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
    @PostMapping("/token")
    public ResponseEntity<Map<String,String>> authenticate(@Valid @RequestBody LogInDto login) {
        String email = login.email();
        String password = login.password();
        String rawToken = email + ":" + password;
        if(userService.validateUser(email, password)) {
            String encodedToken = Base64.getEncoder().encodeToString(rawToken.getBytes());
            Map<String, String> response = new HashMap<>();
            response.put("token", encodedToken);
            return ResponseEntity.ok(response);
        }
        else{
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

    }
}
