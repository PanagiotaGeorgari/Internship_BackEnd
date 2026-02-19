package com.logicea.cards.integration;

import com.logicea.cards.entity.User;
import com.logicea.cards.enums.UserRole;
import com.logicea.cards.repository.UserRepository;
import com.logicea.cards.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest //upload the spring
@ActiveProfiles("test")//use application-test.properties
@Transactional//clean the db after each test
public class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void createUserSuccess() {

        User user = new User();
        user.setName("user");
        user.setPassword("pass");
        user.setEmail("user@gmail.com");
        user.setRole(UserRole.ADMIN);

        User result = userService.createUser(user);

        assertTrue(passwordEncoder.matches("pass", result.getPassword()));

    }

    @Test
    void validateUserSuccess() {
        //data
        String email = "user@gmail.com";
        String password = "pass";

        User user = new User();
        user.setEmail("user@gmail.com");
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(UserRole.ADMIN);
        user.setName("user");

        userRepository.save(user);

        boolean result = userService.validateUser(email, password);

        assertTrue(result);
    }


    @Test
    void validateUserWrongPassword() {

        String email = "test@gmail.com";
        String correctPassword = "pass";
        String wrongPassword = "wrongPass";

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(correctPassword));
        user.setRole(UserRole.ADMIN);
        user.setName("user");

        userRepository.save(user);

        boolean result = userService.validateUser(email, wrongPassword);

        assertFalse(result);
    }

    @Test
    void validateUserNotFound() {

        assertThrows(UsernameNotFoundException.class, () -> userService.validateUser("user@gmail.com", "pass"));
    }

    @Test
    void loadUserByUsernameSuccess() {
        //data
        String email = "test@mail.com";
        User user = new User();
        user.setEmail(email);
        user.setRole(UserRole.ADMIN);
        user.setName("user");
        user.setPassword("encodedPass");

        userRepository.save(user);

        UserDetails result = userService.loadUserByUsername(email);

        //check
        assertEquals(email, result.getUsername());
        assertEquals("encodedPass", result.getPassword());
        assertTrue(result.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));


    }

    @Test
    void loadUserByUsernameUserNotFound() {

        //data
        String email = "notfound@mail.com";

        //check
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(email));
    }

}
