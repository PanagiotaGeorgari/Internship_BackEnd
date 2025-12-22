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

@RestController
@RequestMapping("/user-info")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> createUserInfo(@RequestBody UserDto userDto) {
        UserDto userDtonew = userService.createUser(userDto);
        return new ResponseEntity<>("User " + userDtonew.email() +
                " is registered successfully", HttpStatus.CREATED);
    }
}
