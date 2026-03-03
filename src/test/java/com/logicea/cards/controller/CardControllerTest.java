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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc//spring tool to create fake HTTP requests
@SpringBootTest //upload the spring
@ActiveProfiles("test")//use application-test.properties
@Transactional//clean the db after each test
public class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssocRepository assocRepository;


    private User admin;
    private User member;
    private Card card1;
    private Card card2;


    @BeforeEach
    void setUp() {
        admin = new User();
        admin.setEmail("admin@gmail.com");
        admin.setPassword("password");
        admin.setName("admin");
        admin.setRole(UserRole.ADMIN);
        admin = userRepository.save(admin);

        member = new User();
        member.setEmail("member@gmail.com");
        member.setPassword("password");
        member.setName("member");
        member.setRole(UserRole.MEMBER);
        member = userRepository.save(member);

        card1 = new Card();
        card1.setName("card1");
        card1.setDescription("description1");
        card1.setColor("#abc123");
        card1.setCreatedBy(admin.getUserId());
        card1 = cardRepository.save(card1);

        card2 = new Card();
        card2.setName("card2");
        card2.setDescription("description2");
        card2.setColor("#abc123");
        card2.setCreatedBy(admin.getUserId());
        card2 = cardRepository.save(card2);

    }

    @Test
    void getByIdSuccessAdmin() throws Exception {

        Assoc assoc = new Assoc();
        assoc.setLcardId(card1.getCardId());
        assoc.setRcardId(card2.getCardId());
        assoc.setAssoc(AssocType.BLOCKS);
        assocRepository.save(assoc);

        mockMvc.perform(get("/api/cards/{id}", card1.getCardId())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities())
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.card.name").value("card1"))
                .andExpect(jsonPath("$.assocs.length()").value(1));
    }


    @Test
    void getByIdSuccessMember() throws Exception {

        card1.setCreatedBy(member.getUserId());
        cardRepository.save(card1);

        Assoc assoc = new Assoc();
        assoc.setLcardId(card1.getCardId());
        assoc.setRcardId(card2.getCardId());
        assoc.setAssoc(AssocType.BLOCKS);

        assocRepository.save(assoc);


        mockMvc.perform(get("/api/cards/{id}", card1.getCardId())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities())
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.card.name").value("card1"))
                .andExpect(jsonPath("$.assocs.length()").value(1));

    }

    @Test
    void getByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/cards/{id}", 50)
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities())
                        )))
                .andExpect(status().isNotFound());
    }


    @Test
    void getByIdMemberAccessDenied() throws Exception {

        card1.setCreatedBy(50);

        Assoc assoc = new Assoc();
        assoc.setLcardId(card1.getCardId());
        assoc.setRcardId(card2.getCardId());
        assoc.setAssoc(AssocType.BLOCKS);

        assocRepository.save(assoc);

        mockMvc.perform(get("/api/cards/{id}", card1.getCardId())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities())
                        )))
                .andExpect(status().isForbidden());

    }

    @Test
    void deleteCardNotFound() throws Exception {
        mockMvc.perform(delete("/api/cards/{id}", 50)
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities())
                        )))
                .andExpect(status().isNotFound());

    }

    @Test
    void deleteCardSuccessMember() throws Exception {
        card1.setCreatedBy(member.getUserId());
        cardRepository.save(card1);

        mockMvc.perform(delete("/api/cards/{id}", card1.getCardId())
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities())
                        )))
                .andExpect(status().isOk());

    }

    @Test
    void deleteCardSuccessAdmin() throws Exception {

        mockMvc.perform(delete("/api/cards/{id}", card1.getCardId())
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities())
                        )))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCardMemberNotAccess() throws Exception {
        card1.setCreatedBy(50);
        mockMvc.perform(delete("/api/cards/{id}", card1.getCardId())
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities())
                        )))
                .andExpect(status().isForbidden());
    }

    @Test
    void replaceNotFound() throws Exception {
        Map<String, Object> body = createCardBody("updated", "updated desc", "#abc123");

        mockMvc.perform(put("/api/cards/{id}", 50)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body))
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities())
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updated"))
                .andExpect(jsonPath("$.description").value("updated desc"))
                .andExpect(jsonPath("$.color").value("#abc123"));
    }

    @Test
    void replaceCardSuccessAdmin() throws Exception {

        Map<String, Object> body = createCardBody("updated", "updated desc", "#abc123");

        mockMvc.perform(put("/api/cards/{id}", card1.getCardId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body))
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities())
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updated"));
    }

    @Test
    void replaceCardSuccessMember() throws Exception {

        card1.setCreatedBy(member.getUserId());
        cardRepository.save(card1);

        Map<String, Object> body = createCardBody("updated", "updated desc", "#abc123");

        mockMvc.perform(put("/api/cards/{id}", card1.getCardId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body))
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities())
                        )))
                .andExpect(jsonPath("$.name").value("updated"));
    }

    @Test
    void replaceCardMemberNotAccess() throws Exception {

        Map<String, Object> body = createCardBody("updated", "updated desc", "#abc123");
        card1.setCreatedBy(50);

        mockMvc.perform(put("/api/cards/{id}", card1.getCardId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body))
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities())
                        )))
                .andExpect(status().isForbidden());
    }

    @Test
    void partialUpdateCardNotFound() throws Exception {

        Map<String, Object> body = new HashMap<>();
        body.put("name", "updated field");
        body.put("description", "new card for test update admin");
        body.put("color", "#abc123");

        mockMvc.perform(patch("/api/cards/{id}", 50)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body))
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities())
                        )))
                .andExpect(status().isNotFound());
    }

    @Test
    void partialUpdateCardSuccessAdmin() throws Exception {

        Map<String, Object> body = new HashMap<>();
        body.put("name", "updated");

        mockMvc.perform(patch("/api/cards/{id}", card1.getCardId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body))
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities())
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updated"));
    }

    @Test
    void partialUpdateCardSuccessMember() throws Exception {
        card1.setCreatedBy(member.getUserId());
        card2.setCreatedBy(member.getUserId());
        cardRepository.save(card1);
        cardRepository.save(card2);

        Map<String, Object> body = new HashMap<>();
        body.put("name", "updated");

        mockMvc.perform(patch("/api/cards/{id}", card1.getCardId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body))
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities())
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updated"));
    }


    @Test
    void partialUpdateCardMemberDeniedAccess() throws Exception {

        card1.setName("newname");
        card1.setCreatedBy(50);

        mockMvc.perform(patch("/api/cards/{id}", card1.getCardId())
                        .contentType("application/json")
                        .content("{ \"name\": \"updated field\" }")
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities())
                        )))
                .andExpect(status().isForbidden());
    }

    @Test
    void newCardSuccess() throws Exception {

        Map<String, Object> body = createCardBody("new card", "new description", "#abc123");

        mockMvc.perform(post("/api/cards")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body))
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities())
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("new card"));
    }

    @Test
    void getCardsPaginationAdminSuccess() throws Exception {

        mockMvc.perform(get("/api/cards")
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities())
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void getCardsPaginationMemberSuccess() throws Exception {
        card1.setCreatedBy(member.getUserId());
        card2.setCreatedBy(member.getUserId());
        cardRepository.save(card1);
        cardRepository.save(card2);

        mockMvc.perform(get("/api/cards")
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities())
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void getCardAvailAssocMemberSuccess() throws Exception {

        card1.setCreatedBy(member.getUserId());
        cardRepository.save(card1);

        Card card3 = new Card();
        card3.setName("card3");
        card3.setDescription("card3");
        card3.setColor("#abc123");
        card3.setCreatedBy(member.getUserId());
        card3 = cardRepository.save(card3);

        Assoc assoc = new Assoc();
        assoc.setLcardId(card1.getCardId());
        assoc.setRcardId(card2.getCardId());
        assoc.setAssoc(AssocType.BLOCKS);
        assocRepository.save(assoc);

        mockMvc.perform(get("/api/cards/{id}/assoc-options", card1.getCardId())
                        .param("assoc", "BLOCKS")
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities())
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cards.length()").value(1))
                .andExpect(jsonPath("$.cards[0].id").value(card3.getCardId()));
    }

    @Test
    void getCardAvailAssocAdminSuccess() throws Exception {
        Card card3 = new Card();
        card3.setName("card3");
        card3.setDescription("card3");
        card3.setColor("#abc123");
        card3.setCreatedBy(admin.getUserId());
        card3 = cardRepository.save(card3);

        Assoc assoc = new Assoc();
        assoc.setLcardId(card1.getCardId());
        assoc.setRcardId(card2.getCardId());
        assoc.setAssoc(AssocType.BLOCKS);
        assocRepository.save(assoc);

        mockMvc.perform(get("/api/cards/{id}/assoc-options", card1.getCardId())
                        .param("assoc", "BLOCKS")
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities())
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cards.length()").value(1))
                .andExpect(jsonPath("$.cards[0].id").value(card3.getCardId()));
    }

    @Test
    void getCardAvailAssocMemberAccessDenied() throws Exception {

        card1.setCreatedBy(50);
        cardRepository.save(card1);

        mockMvc.perform(get("/api/cards/{id}/assoc-options", card1.getCardId())
                        .param("assoc", "BLOCKS")
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities())
                        )))
                .andExpect(status().isForbidden());

    }


    @Test
    void getCardAvailAssocPassive() throws Exception {
        Card card3 = new Card();
        card3.setName("card3");
        card3.setDescription("description3");
        card3.setColor("#abc123");
        card3.setCreatedBy(admin.getUserId());
        card3 = cardRepository.save(card3);

        Assoc assoc = new Assoc();
        assoc.setLcardId(card2.getCardId());
        assoc.setRcardId(card1.getCardId());
        assoc.setAssoc(AssocType.BLOCKED_BY);
        assocRepository.save(assoc);

        mockMvc.perform(get("/api/cards/{id}/assoc-options", card1.getCardId())
                        .param("assoc", "BLOCKED_BY")
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities())
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cards").isArray())
                .andExpect(jsonPath("$.cards.length()").value(1))
                .andExpect(jsonPath("$.cards[0].id").value(card3.getCardId()))
                .andExpect(jsonPath("$.cards[0].name").value("card3"));
    }

    private Map<String, Object> createCardBody(String name, String description, String color) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("description", description);
        body.put("color", color);
        return body;
    }

}



