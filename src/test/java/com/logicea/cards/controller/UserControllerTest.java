package com.logicea.cards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicea.cards.entity.User;
import com.logicea.cards.enums.UserRole;
import com.logicea.cards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc//spring tool to create fake HTTP requests
@SpringBootTest //upload the spring
@ActiveProfiles("test")//use application-test.properties
@Transactional//clean the db after each test
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void createUserSuccess() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "user");
        body.put("password", "pass");
        body.put("email", "user@gmail.com");
        body.put("role", "ADMIN");

        mockMvc.perform(post("/user-info/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("user"))
                .andExpect(jsonPath("$.email").value("user@gmail.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));


        User savedUser = userRepository.findByEmail("user@gmail.com")
                .orElseThrow();
        assertEquals("user", savedUser.getName());
        assertTrue(passwordEncoder.matches("pass", savedUser.getPassword()));
    }

    @Test
    void validateUserSuccess() throws Exception {

        String email = "user@gmail.com";
        String password = "pass";

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(UserRole.ADMIN);
        user.setName("user");

        userRepository.save(user);

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        mockMvc.perform(post("/user-info/token")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void validateUserWrongPassword() throws Exception {

        String email = "test@gmail.com";
        String correctPassword = "pass";

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(correctPassword));
        user.setRole(UserRole.ADMIN);
        user.setName("user");
        userRepository.save(user);

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", "wrongPass");

        mockMvc.perform(post("/user-info/token")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loadUserByUsernameSuccess() throws Exception {

        String encodedPassword = passwordEncoder.encode("pass");

        User user = new User();
        user.setEmail("user@gmail.com");
        user.setRole(UserRole.ADMIN);
        user.setName("user");
        user.setPassword(encodedPassword);

        userRepository.save(user);

        Map<String, String> body = new HashMap<>();
        body.put("email", "user@gmail.com");
        body.put("password", "pass");

        mockMvc.perform(post("/user-info/token")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());

    }


}


