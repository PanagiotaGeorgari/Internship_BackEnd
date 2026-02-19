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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc//spring tool to create fake HTTP requests
@SpringBootTest //upload the spring
@ActiveProfiles("test")//use application-test.properties
@Transactional//clean the db after each test
public class CardRestTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssocRepository assocRepository;

    private User currentUser;


    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setEmail("user@gmail.com");
        currentUser.setPassword("password");
        currentUser.setName("user");
        currentUser.setRole(UserRole.ADMIN);
        currentUser = userRepository.save(currentUser);


        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        currentUser,
                        null,
                        List.of(() -> "ROLE_ADMIN")
                );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void getByIdSuccessAdmin() throws Exception {

        Card card1 = new Card();
        card1.setName("new card1");
        card1.setDescription("new description");
        card1.setColor("#abc123");
        card1.setCreatedBy(currentUser.getUserId());
        card1 = cardRepository.save(card1);

        Card card2 = new Card();
        card2.setName("new card2");
        card2.setDescription("new description");
        card2.setColor("#abc123");
        card2.setCreatedBy(currentUser.getUserId());
        card2 = cardRepository.save(card2);


        Assoc assoc = new Assoc();
        assoc.setLcardId(card1.getCardId());
        assoc.setRcardId(card2.getCardId());
        assoc.setAssoc(AssocType.BLOCKS);
        assocRepository.save(assoc);

        mockMvc.perform(get("/api/cards/{id}", card1.getCardId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.card.name").value("new card1"))
                .andExpect(jsonPath("$.card.description").value("new description"))
                .andExpect(jsonPath("$.assocs.length()").value(1))
                .andExpect(jsonPath("$.assocs[0].assoc").value("BLOCKS"))
                .andExpect(jsonPath("$.assocs[0].card.id").value(card2.getCardId()))
                .andExpect(jsonPath("$.assocs[0].card.name").value("new card2"));

    }


    @Test
    void getByIdSuccessMember() throws Exception {
        // data
        currentUser.setRole(UserRole.MEMBER);
        Card card1 = new Card();
        card1.setName("new card1");
        card1.setDescription("new description");
        card1.setColor("#abc123");
        card1.setCreatedBy(currentUser.getUserId());

        Card card2 = new Card();
        card2.setName("new card2");
        card2.setDescription("new description");
        card2.setColor("#abc123");
        card2.setCreatedBy(currentUser.getUserId());

        //save to repository
        card1 = cardRepository.save(card1);
        card2 = cardRepository.save(card2);

        Assoc assoc = new Assoc();
        assoc.setLcardId(card1.getCardId());
        assoc.setRcardId(card2.getCardId());
        assoc.setAssoc(AssocType.BLOCKS);

        assocRepository.save(assoc);

        mockMvc.perform(get("/api/cards/{id}", card1.getCardId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.card.name").value("new card1"))
                .andExpect(jsonPath("$.card.description").value("new description"))
                .andExpect(jsonPath("$.assocs.length()").value(1))
                .andExpect(jsonPath("$.assocs[0].assoc").value("BLOCKS"))
                .andExpect(jsonPath("$.assocs[0].card.id").value(card2.getCardId()))
                .andExpect(jsonPath("$.assocs[0].card.name").value("new card2"));

    }

    @Test
    void getByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/cards/{id}", 50))
                .andExpect(status().isNotFound());
    }


    @Test
    void getByIdMemberAccessDenied() throws Exception {

        currentUser.setRole(UserRole.MEMBER);

        Card card1 = new Card();
        card1.setName("new card1");
        card1.setDescription("new description");
        card1.setColor("#abc123");
        card1.setCreatedBy(50);

        Card card2 = new Card();
        card2.setName("new card2");
        card2.setDescription("new description");
        card2.setColor("#abc123");
        card2.setCreatedBy(currentUser.getUserId());

        Card savedCard1 = cardRepository.save(card1);
        Card savedCard2 = cardRepository.save(card2);

        Assoc assoc = new Assoc();
        assoc.setLcardId(savedCard1.getCardId());
        assoc.setRcardId(savedCard2.getCardId());
        assoc.setAssoc(AssocType.BLOCKS);

        assocRepository.save(assoc);

        mockMvc.perform(get("/api/cards/{id}", savedCard1.getCardId()))
                .andExpect(status().isForbidden());

    }

    @Test
    void deleteCardNotFound() throws Exception {
        mockMvc.perform(delete("/api/cards/{id}", 50))
                .andExpect(status().isNotFound());

    }

    @Test
    void deleteCardSuccessMember() throws Exception {

        currentUser.setRole(UserRole.MEMBER);
        Card card = new Card();
        card.setName("new card1");
        card.setDescription("new description");
        card.setColor("#abc123");
        card.setCreatedBy(currentUser.getUserId());

        cardRepository.save(card);

        mockMvc.perform(delete("/api/cards/{id}", card.getCardId()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCardSuccessAdmin() throws Exception {
        //data
        currentUser.setRole(UserRole.ADMIN);
        Card card = new Card();
        card.setName("new card1");
        card.setDescription("new description");
        card.setColor("#abc123");
        card.setCreatedBy(currentUser.getUserId());
        //save to H2
        cardRepository.save(card);
        //check
        mockMvc.perform(delete("/api/cards/{id}", card.getCardId()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCardMemberNotAccess() throws Exception {
        //data
        currentUser.setRole(UserRole.MEMBER);
        Card card = new Card();
        card.setName("new card1");
        card.setDescription("new description");
        card.setColor("#abc123");
        card.setCreatedBy(50);
        //save to H2
        cardRepository.save(card);
        mockMvc.perform(delete("/api/cards/{id}", card.getCardId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void replaceNotFoundRest() throws Exception {
        mockMvc.perform(put("/api/cards/{id}", 50)
                        .contentType("application/json")
                        .content("{\"name\":\"new card1\",\"description\":\"new description\",\"color\":\"#abc123\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void replaceCardSuccessAdmin() throws Exception {
        currentUser.setRole(UserRole.ADMIN);
        Card newCard = new Card();
        newCard.setName("newCard");
        newCard.setDescription("new card for test update admin");
        newCard.setColor("#abc123");
        newCard.setCreatedBy(currentUser.getUserId());
        cardRepository.save(newCard);

        mockMvc.perform(put("/api/cards/{id}", newCard.getCardId())
                        .contentType("application/json")
                        .content("{\"name\":\"newCard\",\"description\":\"new card for test update admin\",\"color\":\"#abc123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("newCard"))
                .andExpect(jsonPath("$.description").value("new card for test update admin"))
                .andExpect(jsonPath("$.color").value("#abc123"));
    }

    @Test
    void replaceCardSuccessMember() throws Exception {
        currentUser.setRole(UserRole.MEMBER);

        Card newCard = new Card();
        newCard.setName("newCard");
        newCard.setDescription("new card for test update admin");
        newCard.setColor("#abc123");
        newCard.setCreatedBy(currentUser.getUserId());
        cardRepository.save(newCard);

        mockMvc.perform(put("/api/cards/{id}", newCard.getCardId())
                        .contentType("application/json")
                        .content("{\"name\":\"newCard\",\"description\":\"new card for test update admin\",\"color\":\"#abc123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("newCard"))
                .andExpect(jsonPath("$.description").value("new card for test update admin"))
                .andExpect(jsonPath("$.color").value("#abc123"));
    }

    @Test
    void replaceCardMemberNotAccess() throws Exception {
        currentUser.setRole(UserRole.MEMBER);

        Card newCard = new Card();
        newCard.setName("newCard");
        newCard.setDescription("new card for test update admin");
        newCard.setColor("#abc123");
        newCard.setCreatedBy(50);
        cardRepository.save(newCard);

        mockMvc.perform(delete("/api/cards/{id}", newCard.getCardId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void partialUpdateCardNotFound() throws Exception {
        mockMvc.perform(patch("/api/cards/{id}", 50)
                        .contentType("application/json")
                        .content("{\"name\":\"updated field\",\"description\":\"new card for test update admin\",\"color\":\"#abc123\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void partialUpdateCardSuccessAdmin() throws Exception {
        currentUser.setRole(UserRole.ADMIN);
        Card newCard = new Card();
        newCard.setName("newCard");
        newCard.setDescription("new card for test update admin");
        newCard.setColor("#abc123");
        newCard.setCreatedBy(currentUser.getUserId());

        newCard = cardRepository.save(newCard);

        Card newCardPartial = new Card();
        newCardPartial.setName("updated field");
        mockMvc.perform(patch("/api/cards/{id}", newCard.getCardId())
                        .contentType("application/json")
                        .content("{\"name\":\"updated field\",\"description\":\"new card for test update admin\",\"color\":\"#abc123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updated field"))
                .andExpect(jsonPath("$.description").value("new card for test update admin"))
                .andExpect(jsonPath("$.color").value("#abc123"));
    }

    @Test
    void partialUpdateCardSuccessMember() throws Exception {

        currentUser.setRole(UserRole.MEMBER);
        Card newCard = new Card();
        newCard.setName("newCard");
        newCard.setDescription("new card for test update admin");
        newCard.setColor("#abc123");
        newCard.setCreatedBy(currentUser.getUserId());
        newCard = cardRepository.save(newCard);

        Card newCardPartial = new Card();
        newCardPartial.setName("updated field");

        mockMvc.perform(patch("/api/cards/{id}", newCard.getCardId())
                        .contentType("application/json")
                        .content("{\"name\":\"updated field\",\"description\":\"new card for test update admin\",\"color\":\"#abc123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updated field"))
                .andExpect(jsonPath("$.description").value("new card for test update admin"))
                .andExpect(jsonPath("$.color").value("#abc123"));
    }

    @Test
    void partialUpdateCardMemberDeniedAccess() throws Exception {

        currentUser.setRole(UserRole.MEMBER);
        Card newCard = new Card();
        newCard.setName("newCard");
        newCard.setDescription("new card for test");
        newCard.setColor("#abc123");
        newCard.setCreatedBy(50);
        newCard = cardRepository.save(newCard);

        mockMvc.perform(patch("/api/cards/{id}", newCard.getCardId())
                        .contentType("application/json")
                        .content("{ \"name\": \"updated field\" }")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void newCardSuccess() throws Exception {
        Card newCard = new Card();
        newCard.setName("new card name");
        newCard.setDescription("new card");
        newCard.setColor("#abc123");
        newCard.setCreatedBy(currentUser.getUserId());
        cardRepository.save(newCard);

        mockMvc.perform(post("/api/cards")
                        .contentType("application/json")
                        .content("{\"name\":\"new card name\",\"description\":\"new card\",\"color\":\"#abc123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("new card name"))
                .andExpect(jsonPath("$.description").value("new card"))
                .andExpect(jsonPath("$.color").value("#abc123"));
    }

    @Test
    void getCardsPaginationAdminSuccess() throws Exception {

        currentUser.setRole(UserRole.ADMIN);
        Card card1 = new Card();
        card1.setName("card1");
        card1.setDescription("card1");
        card1.setColor("#abc123");
        card1.setCreatedBy(currentUser.getUserId());
        Card card2 = new Card();
        card2.setName("card2");
        card2.setDescription("card2");
        card2.setColor("#abc123");
        card2.setCreatedBy(currentUser.getUserId());
        cardRepository.save(card1);
        cardRepository.save(card2);

        mockMvc.perform(get("/api/cards")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(card1.getCardId()))
                .andExpect(jsonPath("$.data[0].name").value("card1"))
                .andExpect(jsonPath("$.data[1].id").value(card2.getCardId()))
                .andExpect(jsonPath("$.data[1].name").value("card2"));
    }

    @Test
    void getCardsPaginationMemberSuccess() throws Exception {

        currentUser.setRole(UserRole.MEMBER);
        Card card1 = new Card();
        card1.setName("card1");
        card1.setDescription("card1");
        card1.setColor("#abc123");
        card1.setCreatedBy(currentUser.getUserId());
        Card card2 = new Card();
        card2.setName("card2");
        card2.setDescription("card2");
        card2.setColor("#abc123");
        card2.setCreatedBy(currentUser.getUserId());
        cardRepository.save(card1);
        cardRepository.save(card2);

        mockMvc.perform(get("/api/cards")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(card1.getCardId()))
                .andExpect(jsonPath("$.data[0].name").value("card1"))
                .andExpect(jsonPath("$.data[1].id").value(card2.getCardId()))
                .andExpect(jsonPath("$.data[1].name").value("card2"));
    }

    @Test
    void getCardAvailAssocMemberSuccess() throws Exception {
        currentUser.setRole(UserRole.MEMBER);
        Card card1 = new Card();
        card1.setName("card1");
        card1.setDescription("card1");
        card1.setColor("#abc123");
        card1.setCreatedBy(currentUser.getUserId());

        Card card2 = new Card();
        card2.setName("card2");
        card2.setDescription("card2");
        card2.setColor("#abc123");
        card2.setCreatedBy(currentUser.getUserId());

        Card card3 = new Card();
        card3.setName("card3");
        card3.setDescription("card3");
        card3.setColor("#abc123");
        card3.setCreatedBy(currentUser.getUserId());

        cardRepository.save(card1);
        cardRepository.save(card2);
        cardRepository.save(card3);

        Assoc assoc = new Assoc();
        assoc.setLcardId(card1.getCardId());
        assoc.setRcardId(card2.getCardId());
        assoc.setAssoc(AssocType.BLOCKS);
        assocRepository.save(assoc);

        mockMvc.perform(get("/api/cards/{id}/assoc-options", card1.getCardId())
                        .param("assoc", "BLOCKS")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cards").isArray())
                .andExpect(jsonPath("$.cards.length()").value(1))
                .andExpect(jsonPath("$.cards[0].id").value(card3.getCardId()))
                .andExpect(jsonPath("$.cards[0].name").value("card3"));
    }

    @Test
    void getCardAvailAssocAdminSuccess() throws Exception {

        currentUser.setRole(UserRole.ADMIN);
        Card card1 = new Card();
        card1.setName("Card1");
        card1.setDescription("Card1");
        card1.setColor("#abc123");
        card1.setCreatedBy(currentUser.getUserId());

        Card card2 = new Card();
        card2.setName("Card2");
        card2.setDescription("Card2");
        card2.setColor("#abc123");
        card2.setCreatedBy(currentUser.getUserId());

        Card card3 = new Card();
        card3.setName("Card3");
        card3.setDescription("Card3");
        card3.setColor("#abc123");
        card3.setCreatedBy(currentUser.getUserId());

        cardRepository.save(card1);
        cardRepository.save(card2);
        cardRepository.save(card3);

        int card1Id = card1.getCardId();
        int card2Id = card2.getCardId();

        Assoc assoc = new Assoc();
        assoc.setLcardId(card1Id);
        assoc.setRcardId(card2Id);
        assoc.setAssoc(AssocType.BLOCKS);
        assocRepository.save(assoc);

        mockMvc.perform(get("/api/cards/{id}/assoc-options", card1.getCardId())
                        .param("assoc", "BLOCKS")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cards").isArray())
                .andExpect(jsonPath("$.cards.length()").value(1))
                .andExpect(jsonPath("$.cards[0].id").value(card3.getCardId()))
                .andExpect(jsonPath("$.cards[0].name").value("Card3"));
    }

    @Test
    void getCardAvailAssocMemberAccessDenied() throws Exception {

        currentUser.setRole(UserRole.MEMBER);

        User user2 = new User();
        user2.setEmail("user2@gmail.com");
        user2.setName("user2");
        user2.setPassword("pass");
        user2.setRole(UserRole.MEMBER);
        userRepository.save(user2);

        Card card = new Card();
        card.setName("new Card ");
        card.setDescription("new Card");
        card.setColor("#abc123");
        card.setCreatedBy(user2.getUserId());
        cardRepository.save(card);

        mockMvc.perform(get("/api/cards/{id}", card.getCardId()))
                .andExpect(status().isForbidden());
    }


    @Test
    void getCardAvailAssocPassive() throws Exception {

        currentUser.setRole(UserRole.MEMBER);

        Card card1 = new Card();
        card1.setName("Card1");
        card1.setDescription("Card1");
        card1.setColor("#123");
        card1.setCreatedBy(currentUser.getUserId());

        Card card2 = new Card();
        card2.setName("Card2");
        card2.setDescription("Card2");
        card2.setColor("#456");
        card2.setCreatedBy(currentUser.getUserId());

        Card card3 = new Card();
        card3.setName("Card3");
        card3.setDescription("Card3");
        card3.setColor("#789");
        card3.setCreatedBy(currentUser.getUserId());

        cardRepository.saveAll(List.of(card1, card2, card3));

        Assoc assoc = new Assoc();
        assoc.setLcardId(card2.getCardId());
        assoc.setRcardId(card1.getCardId());
        assoc.setAssoc(AssocType.BLOCKED_BY);
        assocRepository.save(assoc);

        mockMvc.perform(get("/api/cards/{id}/assoc-options", card1.getCardId())
                        .param("assoc", "BLOCKED_BY")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cards").isArray())
                .andExpect(jsonPath("$.cards.length()").value(1))
                .andExpect(jsonPath("$.cards[0].id").value(card3.getCardId()))
                .andExpect(jsonPath("$.cards[0].name").value("Card3"));
    }

}



