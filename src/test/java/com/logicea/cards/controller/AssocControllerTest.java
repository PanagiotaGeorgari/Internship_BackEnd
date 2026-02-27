package com.logicea.cards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc//spring tool to create fake HTTP requests
@SpringBootTest //upload the spring
@ActiveProfiles("test")//use application-test.properties
@Transactional//clean the db after each test
public class AssocControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AssocRepository assocRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private User admin;
    private User member;
    private Card card1;
    private Card card2;

    @BeforeEach
    void setup() {
        admin = new User();
        admin.setRole(UserRole.ADMIN);
        admin.setName("admin");
        admin.setEmail("admin@gmail.com");
        admin.setPassword(passwordEncoder.encode("pass"));
        userRepository.save(admin);

        member = new User();
        member.setRole(UserRole.MEMBER);
        member.setName("member");
        member.setEmail("member@gmail.com");
        member.setPassword(passwordEncoder.encode("pass"));
        userRepository.save(member);

        card1 = new Card();
        card1.setName("card1");
        card1.setCreatedBy(admin.getUserId());
        cardRepository.save(card1);

        card2 = new Card();
        card2.setName("card2");
        card2.setCreatedBy(admin.getUserId());
        cardRepository.save(card2);
    }

    @Test
    void newAssocRightNotFound() throws Exception {
        Map<String, Object> body = createjsonBody(1, card1.getCardId(), 999, "BLOCKS");
        mockMvc.perform(post("/card-assocs")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body))
                        .with(csrf())
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void newAssocLeftNotFound() throws Exception {
        Map<String, Object> body = createjsonBody(1, 999, card2.getCardId(), "BLOCKS");
        mockMvc.perform(post("/card-assocs")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body))
                        .with(csrf())
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void newAssocAccessDenied() throws Exception {

        card2.setCreatedBy(50);

        Assoc assoc = new Assoc();
        assoc.setLcardId(card2.getCardId());
        assoc.setRcardId(card1.getCardId());
        assoc.setAssoc(AssocType.BLOCKS);

        Map<String, Object> body = createjsonBody(1, card2.getCardId(), card1.getCardId(), "BLOCKS");
        mockMvc.perform(post("/card-assocs")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body))
                        .with(csrf())
                        .with(authentication(new UsernamePasswordAuthenticationToken(member, null, List.of(new SimpleGrantedAuthority("ROLE_MEMBER"))))))
                .andExpect(status().isForbidden());
    }

    @Test
    void newAssocAlreadyAssociated() throws Exception {

        Assoc assoc = new Assoc();
        assoc.setLcardId(card1.getCardId());
        assoc.setRcardId(card2.getCardId());
        assoc.setAssoc(AssocType.BLOCKS);
        assocRepository.save(assoc);

        Map<String, Object> body = createjsonBody(4, card1.getCardId(), card2.getCardId(), "BLOCKS");
        mockMvc.perform(post("/card-assocs")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body))
                        .with(csrf())
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                admin, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                        ))))
                .andExpect(status().isConflict())
                .andExpect(content().string("Assoc Already exists!"));
    }

    @Test
    void newAssocSuccess() throws Exception {

        Map<String, Object> body = createjsonBody(1, card1.getCardId(), card2.getCardId(), "BLOCKS");
        Card lcard = new Card();
        lcard.setName("card1");


        Card rcard = new Card();
        rcard.setName("card2");


        mockMvc.perform(post("/card-assocs")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body))
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities())))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids[0]").value(1))
                .andExpect(jsonPath("$.ids[1]").value(2));
    }

    @Test
    void validateOwnerAdmin() throws Exception {
        Map<String, Object> body = createjsonBody(0L, card1.getCardId(), card2.getCardId(), "BLOCKS");

        mockMvc.perform(post("/card-assocs")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)).with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities())
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids.length()").value(2));

    }

    @Test
    void validateOwnerMemberAccess() throws Exception {
        card1.setCreatedBy(member.getUserId());
        card2.setCreatedBy(member.getUserId());
        cardRepository.save(card1);
        cardRepository.save(card2);

        Map<String, Object> body = createjsonBody(0, card1.getCardId(), card2.getCardId(), "BLOCKS");
        mockMvc.perform(post("/card-assocs")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)).with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities())
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids.length()").value(2));

    }

    @Test
    void validateOwnerMemberNotAccess() throws Exception {
        card1.setCreatedBy(member.getUserId());
        card2.setCreatedBy(50);
        cardRepository.save(card1);
        cardRepository.save(card2);

        Map<String, Object> body = createjsonBody(0, card1.getCardId(), card2.getCardId(), "BLOCKS");

        mockMvc.perform(post("/card-assocs")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)).with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities())
                        )))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteAssocNotFound() throws Exception {
        mockMvc.perform(delete("/card-assocs/{id}", 50)
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities())
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
        inverseAssoc.setAssoc(AssocType.BLOCKED_BY);
        assocRepository.save(inverseAssoc);

        mockMvc.perform(delete("/card-assocs/{id}", assoc.getId())
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities()))))
                .andExpect(status().isOk());

        assertFalse(assocRepository.findById(assoc.getId()).isPresent());
        assertFalse(assocRepository.findById(inverseAssoc.getId()).isPresent());
    }

    @Test
    void deleteAssocMember() throws Exception {
        card1.setCreatedBy(member.getUserId());
        card2.setCreatedBy(member.getUserId());
        cardRepository.save(card1);
        cardRepository.save(card2);

        Assoc assoc = new Assoc();
        assoc.setLcardId(card2.getCardId());
        assoc.setRcardId(card1.getCardId());
        assoc.setAssoc(AssocType.BLOCKS);
        assoc = assocRepository.save(assoc);

        Assoc assoc2 = new Assoc();
        assoc2.setLcardId(card1.getCardId());
        assoc2.setRcardId(card2.getCardId());
        assoc2.setAssoc(AssocType.BLOCKED_BY);
        assocRepository.save(assoc2);


        mockMvc.perform(delete("/card-assocs/{id}", assoc.getId())
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities()))))
                .andExpect(status().isOk());

        assertFalse(assocRepository.findById(assoc.getId()).isPresent());
        assertFalse(assocRepository.findById(assoc2.getId()).isPresent());

    }

    @Test
    void deleteAssocMemberNotOwner() throws Exception {

        card1.setCreatedBy(member.getUserId());
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
                        .with(authentication(new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities())))
                )
                .andExpect(status().isForbidden());
    }

    private Map<String, Object> createjsonBody(long id, long lcardId, long rcardId, String type) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("lcardId", lcardId);
        body.put("rcardId", rcardId);
        body.put("assoc", type);
        return body;
    }

}

