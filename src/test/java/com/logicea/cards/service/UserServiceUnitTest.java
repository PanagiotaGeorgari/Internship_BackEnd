package com.logicea.cards.service;

import com.logicea.cards.entity.User;
import com.logicea.cards.repository.UserRepository;
import com.logicea.cards.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // activate mockito into junit
public class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUserSuccess() {
        //data
        User user = new User();
        user.setUserId(1);
        user.setName("user");
        user.setPassword("pass");

        //action
        when(passwordEncoder.encode("pass"))
                .thenReturn("encoded");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        User result = userService.createUser(user);

        //check
        assertEquals("encoded", result.getPassword());

    }

    @Test
    void validateUserSuccess() {
        //data
        String email = "user@gmail.com";
        String password = "pass";
        String encodedPassword = "encoded";

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);

        //action
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(password, encodedPassword))
                .thenReturn(true);

        // when
        boolean result = userService.validateUser(email, password);

        // then
        assertTrue(result);
    }

    @Test
    void validateUserWrongPassword() {
        //data
        String email = "test@gmail.com";
        String password = "pass";
        String encodedPassword = "encoded";

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);

        //action
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(password, encodedPassword))
                .thenReturn(false);


        boolean result = userService.validateUser(email, password);

        //check
        assertFalse(result);
    }

    @Test
    void validateUserNotFound() {

        //data
        String email = "user@gmail.com";
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());
        //check
        assertThrows(UsernameNotFoundException.class, () -> userService.validateUser("user@gmail.com", "pass"));
    }

    @Test
    void loadUserByUsernameSuccess() {
        //data
        String email = "test@mail.com";
        User user = new User();
        user.setEmail(email);
        user.setPassword("encodedPass");

        //action
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        UserDetails result = userService.loadUserByUsername(email);

        //check
        assertEquals(email, result.getUsername());


    }

    @Test
    void loadUserByUsernameUserNotFound() {

        //data
        String email = "notfound@mail.com";

        //action
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        //check
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(email));
    }


}
