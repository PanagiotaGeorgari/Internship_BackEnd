package com.logicea.cards.controller;
import com.logicea.cards.dto.UserDto;
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
    public ResponseEntity<Map<String, String>> createUserInfo(@RequestBody UserDto userDto) {
        UserDto userDtonew = userService.createUser(userDto);
        String rawToken = userDtonew.email() + ":" +userDto.password();
        String encodedToken = Base64.getEncoder().encodeToString(rawToken.getBytes());
        Map<String, String> response = new HashMap<>();
        response.put("token", encodedToken);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
