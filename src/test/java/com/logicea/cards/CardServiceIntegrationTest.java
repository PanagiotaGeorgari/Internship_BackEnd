package com.logicea.cards;

import com.logicea.cards.entity.Assoc;
import com.logicea.cards.entity.Card;
import com.logicea.cards.entity.User;
import com.logicea.cards.enums.AssocType;
import com.logicea.cards.enums.UserRole;
import com.logicea.cards.repository.AssocRepository;
import com.logicea.cards.repository.CardRepository;
import com.logicea.cards.repository.UserRepository;
import com.logicea.cards.service.impl.CardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest //upload the spring
@ActiveProfiles("test")//use application-test.properties
@Transactional//clean the db after each test
class CardServiceIntegrationTest {

    @Autowired
    private CardServiceImpl cardService;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssocRepository assocRepository;


    private User currentUser;

    @BeforeEach
    void setUp() {
        //data
        currentUser = new User();
        currentUser.setEmail("user@gmail.com");
        currentUser.setPassword("password");
        currentUser.setName("user");
        currentUser.setRole(UserRole.ADMIN);

        //save the user in H2 db
        userRepository.save(currentUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(currentUser, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void getByIdSuccessAdmin() {
        // data
        Card card1 = new Card();
        card1.setName("new card1");
        card1.setDescription("new description");
        card1.setColor("#abc123");
        card1.setCreatedBy(currentUser.getUserId());
        cardRepository.save(card1);

        Card card2 = new Card();
        card2.setName("new card2");
        card2.setDescription("new description");
        card2.setColor("#abc123");
        card2.setCreatedBy(currentUser.getUserId());
        cardRepository.save(card2);


        Assoc assoc = new Assoc();
        assoc.setId(1);
        assoc.setRcardId(card1.getCardId());
        assoc.setLcardId(card2.getCardId());
        assoc.setAssoc(AssocType.BLOCKS);
        assocRepository.save(assoc);

        // call service method
        GetByIdResponse response = cardService.getById(card1.getCardId());

        // check
        assertNotNull(response);
        assertEquals("new card", response.getCard().getName());
        assertEquals(1, response.getAssocs().size());
    }

}
