package com.logicea.cards.rest;

import com.logicea.cards.entity.Assoc;
import com.logicea.cards.entity.Card;
import com.logicea.cards.entity.User;
import com.logicea.cards.enums.AssocType;
import com.logicea.cards.enums.UserRole;
import com.logicea.cards.repository.AssocRepository;
import com.logicea.cards.repository.CardRepository;
import com.logicea.cards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc//spring tool to create fake HTTP requests
@SpringBootTest //upload the spring
@ActiveProfiles("test")//use application-test.properties
@Transactional//clean the db after each test
public class AssocRestTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AssocRepository assocRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;
    private Card card1;
    private Card card2;

    @BeforeEach
    void setup() {
        user = new User();
        user.setName("user");
        user.setEmail("user@gmail.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(UserRole.ADMIN);
        userRepository.save(user);

        card1 = new Card();
        card1.setName("card1");
        card1.setCreatedBy(user.getUserId());
        cardRepository.save(card1);

        card2 = new Card();
        card2.setName("card2");
        card2.setCreatedBy(user.getUserId());
        cardRepository.save(card2);
    }

    @Test
    void newAssocRightNotFound() throws Exception {
        mockMvc.perform(post("/card-assocs")
                        .contentType("application/json")
                        .content(String.format(
                                "{\"id\":1,\"lcardId\":%d,\"rcardId\":999,\"assoc\":\"BLOCKS\"}",
                                card1.getCardId()
                        ))
                        .with(csrf())
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void newAssocLeftNotFound() throws Exception {
        mockMvc.perform(post("/card-assocs")
                        .contentType("application/json")
                        .content(String.format(
                                "{\"id\":1,\"lcardId\":999,\"rcardId\":%d,\"assoc\":\"BLOCKS\"}",
                                card2.getCardId()
                        ))
                        .with(csrf())
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void newAssocAccessDenied() throws Exception {

        user.setRole(UserRole.MEMBER);
        card2.setCreatedBy(50);

        Assoc assoc = new Assoc();
        assoc.setLcardId(card2.getCardId());
        assoc.setRcardId(card1.getCardId());
        assoc.setAssoc(AssocType.BLOCKS);


        mockMvc.perform(post("/card-assocs")
                        .contentType("application/json")
                        .content(String.format(
                                "{\"id\":1,\"lcardId\":%d,\"rcardId\":%d,\"assoc\":\"BLOCKS\"}",
                                card2.getCardId(), card1.getCardId()
                        ))
                        .with(csrf())
                        .with(authentication(new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ROLE_MEMBER"))))))
                .andExpect(status().isForbidden());
    }

    @Test
    void newAssocAlreadyAssociated() throws Exception {

        Assoc assoc = new Assoc();
        assoc.setLcardId(card1.getCardId());
        assoc.setRcardId(card2.getCardId());
        assoc.setAssoc(AssocType.BLOCKS);
        assocRepository.save(assoc);

        mockMvc.perform(post("/card-assocs")
                        .contentType("application/json")
                        .content(String.format(
                                "{\"id\":4,\"lcardId\":%d,\"rcardId\":%d,\"assoc\":\"BLOCKS\"}",
                                card1.getCardId(), card2.getCardId()
                        ))
                        .with(csrf())
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                user, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                        ))))
                .andExpect(status().isConflict())
                .andExpect(content().string("Assoc Already exists!"));
    }

    @Test
    void newAssocSuccess() throws Exception {

        Card lcard = new Card();
        lcard.setName("card1");
        lcard = cardRepository.save(lcard);

        Card rcard = new Card();
        rcard.setName("card2");
        rcard = cardRepository.save(rcard);

        mockMvc.perform(post("/card-assocs")
                        .contentType("application/json")
                        .content(String.format(
                                "{\"id\":1,\"lcardId\":%d,\"rcardId\":%d,\"assoc\":\"BLOCKS\"}",
                                lcard.getCardId(), rcard.getCardId()
                        ))
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids[0]").value(1))
                .andExpect(jsonPath("$.ids[1]").value(2));
    }

    @Test
    void validateOwnerAdmin() throws Exception {

        mockMvc.perform(post("/card-assocs")
                        .contentType("application/json")
                        .content(String.format("{\"id\":0,\"lcardId\":%d,\"rcardId\":%d,\"assoc\":\"BLOCKS\"}", card1.getCardId(), card2.getCardId()))
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids.length()").value(2));

    }

    @Test
    void validateOwnerMemberAccess() throws Exception {
        user.setRole(UserRole.MEMBER);

        mockMvc.perform(post("/card-assocs")
                        .contentType("application/json")
                        .content(String.format("{\"id\":0,\"lcardId\":%d,\"rcardId\":%d,\"assoc\":\"BLOCKS\"}", card1.getCardId(), card2.getCardId()))
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids.length()").value(2));

    }

    @Test
    void validateOwnerMemberNotAccess() throws Exception {
        user.setRole(UserRole.MEMBER);
        card1.setCreatedBy(user.getUserId());
        card2.setCreatedBy(50);

        mockMvc.perform(post("/card-assocs")
                        .contentType("application/json")
                        .content(String.format("{\"id\":0,\"lcardId\":%d,\"rcardId\":%d,\"assoc\":\"BLOCKS\"}", card1.getCardId(), card2.getCardId()))
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
                        )))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteAssocNotFound() throws Exception {

        User user = new User();
        user.setEmail("email@gmail.com");
        user.setName("user");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(UserRole.ADMIN);
        user = userRepository.save(user);

        mockMvc.perform(delete("/card-assocs/{id}", 50)
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
                        )))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAssocAdmin() throws Exception {

        Assoc assoc = new Assoc();
        assoc.setLcardId(card1.getCardId());
        assoc.setRcardId(card2.getCardId());
        assoc.setAssoc(AssocType.BLOCKS);
        assoc = assocRepository.save(assoc);

        Assoc inverseAssoc = new Assoc();
        inverseAssoc.setLcardId(card2.getCardId());
        inverseAssoc.setRcardId(card1.getCardId());
        inverseAssoc.setAssoc(AssocType.BLOCKS);
        assocRepository.save(inverseAssoc);

        mockMvc.perform(delete("/card-assocs/{id}", assoc.getId())
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()))))
                .andExpect(status().isOk());

        assertFalse(assocRepository.findById(assoc.getId()).isPresent());
        assertFalse(assocRepository.findById(inverseAssoc.getId()).isPresent());
    }

    @Test
    void deleteAssocMember() throws Exception {

        Assoc assoc = new Assoc();
        assoc.setLcardId(card2.getCardId());
        assoc.setRcardId(card1.getCardId());
        assoc.setAssoc(AssocType.BLOCKS);
        assoc = assocRepository.save(assoc);

        Assoc assoc2 = new Assoc();
        assoc2.setLcardId(card1.getCardId());
        assoc2.setRcardId(card2.getCardId());
        assoc2.setAssoc(AssocType.BLOCKS);
        assocRepository.save(assoc2);


        mockMvc.perform(delete("/card-assocs/{id}", assoc.getId())
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()))))
                .andExpect(status().isOk());

        assertFalse(assocRepository.findById(assoc.getId()).isPresent());
        assertFalse(assocRepository.findById(assoc.getId()).isPresent());

    }

    @Test
    void deleteAssocMemberNotOwner() throws Exception {
        user.setRole(UserRole.MEMBER);

        card1.setCreatedBy(user.getUserId());
        cardRepository.save(card1);

        card2.setCreatedBy(50);
        cardRepository.save(card2);

        Assoc assoc = new Assoc();
        assoc.setLcardId(card2.getCardId());
        assoc.setRcardId(card1.getCardId());
        assoc.setAssoc(AssocType.BLOCKS);
        assocRepository.save(assoc);

        mockMvc.perform(delete("/card-assocs/{id}", assoc.getId())
                        .with(csrf())
                        .with(authentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())))
                )
                .andExpect(status().isForbidden());
    }

}

