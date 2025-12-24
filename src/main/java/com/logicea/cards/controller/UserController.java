package com.logicea.cards.controller;
import com.logicea.cards.dto.UserDto;
import com.logicea.cards.entity.User;
import com.logicea.cards.service.UserService;
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
        UserDto newUser = userService.createUser(userDto);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
    @PostMapping("/token")
    public ResponseEntity<Map<String,String>> authenticate(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        String rawToken = email + ":" + password;
        String encodedToken = Base64.getEncoder().encodeToString(rawToken.getBytes());
        Map<String, String> response = new HashMap<>();
        response.put("token", encodedToken);
        return ResponseEntity.ok(response);
    }
}
