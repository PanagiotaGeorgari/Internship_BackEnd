package com.logicea.cards.service;

import com.logicea.cards.CardNotFoundException;
import com.logicea.cards.GetByIdResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest //upload the spring
@ActiveProfiles("test")//use application-test.properties
@Transactional//clean the db after each test
class CardServiceDbTest {

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

        // call service method
        GetByIdResponse response = cardService.getById(card1.getCardId());

        // check
        assertNotNull(response);
        assertEquals(card1, response.getCard());
        assertEquals(1, response.getAssocs().size());
    }

    @Test
    void getByIdSuccessMember() {
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

        // call service method
        GetByIdResponse response = cardService.getById(card1.getCardId());

        // check
        assertNotNull(response);
        assertEquals(card1, response.getCard());
        assertEquals(1, response.getAssocs().size());
    }

    @Test
    void getByIdNotFound() throws CardNotFoundException {

        assertThrows(CardNotFoundException.class, () -> cardService.getById(20));

    }

    @Test
    void getByIdMemberAccessDenied() {
        // data
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

        //save to H2
        Card savedCard1 = cardRepository.save(card1);
        Card savedCard2 = cardRepository.save(card2);

        Assoc assoc = new Assoc();
        assoc.setLcardId(savedCard1.getCardId());
        assoc.setRcardId(savedCard2.getCardId());
        assoc.setAssoc(AssocType.BLOCKS);
        //save to H2
        assocRepository.save(assoc);

        //check
        assertThrows(AccessDeniedException.class, () -> cardService.getById(savedCard1.getCardId()));
    }

    @Test
    void deleteCardNotFound() throws CardNotFoundException {
        assertThrows(CardNotFoundException.class, () -> cardService.deleteCard(20));
    }

    @Test
    void deleteCardSuccessMember() {
        //data
        currentUser.setRole(UserRole.MEMBER);
        Card card = new Card();
        card.setName("new card1");
        card.setDescription("new description");
        card.setColor("#abc123");
        card.setCreatedBy(currentUser.getUserId());
        //save to H2
        cardRepository.save(card);
        //check
        assertDoesNotThrow(() -> cardService.deleteCard(card.getCardId()));

    }

    @Test
    void deleteCardSuccessAdmin() {
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
        assertDoesNotThrow(() -> cardService.deleteCard(card.getCardId()));

    }

    @Test
    void deleteCardMemberNotAccess() throws AccessDeniedException {
        //data
        currentUser.setRole(UserRole.MEMBER);
        Card card = new Card();
        card.setName("new card1");
        card.setDescription("new description");
        card.setColor("#abc123");
        card.setCreatedBy(50);
        //save to H2
        cardRepository.save(card);
        //check
        assertThrows(AccessDeniedException.class, () -> cardService.deleteCard(card.getCardId()));
    }

    @Test
    void replaceNotFound() throws CardNotFoundException {
        Card card = new Card();
        card.setName("new card1");
        card.setDescription("new description");
        card.setColor("#abc123");
        card.setCreatedBy(currentUser.getUserId());
        assertThrows(CardNotFoundException.class, () -> cardService.replaceCard(card, card.getCardId()));
    }

    @Test
    void replaceCardSuccessAdmin() {

        currentUser.setRole(UserRole.ADMIN);

        Card newCard = new Card();
        newCard.setName("newCard");
        newCard.setDescription("new card for test update admin");
        newCard.setColor("#abc123");
        newCard.setCreatedBy(currentUser.getUserId());
        cardRepository.save(newCard);
        Card result = cardService.replaceCard(newCard, newCard.getCardId());
        assertEquals("newCard", result.getName());
        assertEquals("new card for test update admin", result.getDescription());
        assertEquals("#abc123", result.getColor());

    }

    @Test
    void replaceCardSuccessMember() {

        currentUser.setRole(UserRole.MEMBER);

        Card newCard = new Card();
        newCard.setName("newCard");
        newCard.setDescription("new card for test update admin");
        newCard.setColor("#abc123");
        newCard.setCreatedBy(currentUser.getUserId());
        cardRepository.save(newCard);
        Card result = cardService.replaceCard(newCard, newCard.getCardId());
        assertEquals("newCard", result.getName());
        assertEquals("new card for test update admin", result.getDescription());
        assertEquals("#abc123", result.getColor());


    }

    @Test
    void replaceCardMemberNotAccess() {
        currentUser.setRole(UserRole.MEMBER);

        Card newCard = new Card();
        newCard.setName("newCard");
        newCard.setDescription("new card for test update admin");
        newCard.setColor("#abc123");
        newCard.setCreatedBy(50);
        cardRepository.save(newCard);
        //check
        assertThrows(AccessDeniedException.class, () -> cardService.replaceCard(newCard, newCard.getCardId()));
    }

    @Test
    void partialUpdateCardNotFound() {
        Card card = new Card();
        card.setName("new card1");
        card.setDescription("new description");
        card.setColor("#abc123");
        card.setCreatedBy(currentUser.getUserId());

        assertThrows(CardNotFoundException.class, () -> cardService.partialUpdateCard(card, card.getCardId()));

    }

    @Test
    void partialUpdateCardSuccessAdmin() {
        // data
        currentUser.setRole(UserRole.ADMIN);
        Card newCard = new Card();
        newCard.setName("newCard");
        newCard.setDescription("new card for test update admin");
        newCard.setColor("#abc123");
        newCard.setCreatedBy(currentUser.getUserId());

        newCard = cardRepository.save(newCard);

        Card newCardPartial = new Card();
        newCardPartial.setName("updated field");


        Card result = cardService.partialUpdateCard(newCardPartial, newCard.getCardId());

        // check
        assertEquals("updated field", result.getName());
        assertEquals("new card for test update admin", result.getDescription());
        assertEquals("#abc123", result.getColor());
    }

    @Test
    void partialUpdateCardSuccessMember() {
        // data
        currentUser.setRole(UserRole.MEMBER);
        Card newCard = new Card();
        newCard.setName("newCard");
        newCard.setDescription("new card for test update admin");
        newCard.setColor("#abc123");
        newCard.setCreatedBy(currentUser.getUserId());


        newCard = cardRepository.save(newCard);

        Card newCardPartial = new Card();
        newCardPartial.setName("updated field");


        Card result = cardService.partialUpdateCard(newCardPartial, newCard.getCardId());

        // check
        assertEquals("updated field", result.getName());
        assertEquals("new card for test update admin", result.getDescription());
        assertEquals("#abc123", result.getColor());
    }

    @Test
    void partialUpdateCardMemberDeniedAccess() {
        currentUser.setRole(UserRole.MEMBER);
        Card newCard = new Card();
        newCard.setName("newCard");
        newCard.setDescription("new card for test update admin");
        newCard.setColor("#abc123");
        newCard.setCreatedBy(50);
        cardRepository.save(newCard);

        //check
        assertThrows(AccessDeniedException.class, () -> cardService.partialUpdateCard(newCard, newCard.getCardId()));

    }

    @Test
    void newCardSuccess() {
        Card newCard = new Card();
        newCard.setName("new card name");
        newCard.setDescription("new card");
        newCard.setColor("#abc123");
        newCard.setCreatedBy(currentUser.getUserId());

        cardRepository.save(newCard);


        Card result = cardService.newCard(newCard);

        //check
        assertEquals("new card name", result.getName());
        assertEquals("new card", result.getDescription());
        assertEquals("#abc123", result.getColor());


    }

    @Test
    void getCurrentUserSuccessIntegration() {

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(currentUser, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User result = cardService.getCurrentUser();

        assertNotNull(result);
        assertEquals(currentUser.getUserId(), result.getUserId());
        assertEquals(currentUser.getEmail(), result.getEmail());
    }

    @Test
    void getCurrentUserNotLoggedInIntegration() {

        Authentication authentication =
                new UsernamePasswordAuthenticationToken("anonymousUser", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThrows(ClassCastException.class, () -> cardService.getCurrentUser());
    }

    @Test
    void getCardsPaginationAdminSuccess() {


        //data
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

        //action
        cardRepository.findAll();

        Page<Card> result = cardService.getCardsPagination(0, 10, "cardId");

        //check
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().contains(card1));
        assertTrue(result.getContent().contains(card2));
    }

    @Test
    void getCardsPaginationMemberSuccess() {


        //data
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

        //action
        cardRepository.findAll();

        Page<Card> result = cardService.getCardsPagination(0, 10, "cardId");

        //check
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().contains(card1));
        assertTrue(result.getContent().contains(card2));
    }

    @Test
    void getCardAvailAssocMemberSuccess() {

        //data
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

        List<Card> result = cardService.getCardAvailAssoc(card1.getCardId(), AssocType.BLOCKS);

        // check
        assertEquals(1, result.size());
        assertEquals(card3.getCardId(), result.getFirst().getCardId());

    }

    @Test
    void getCardAvailAssocAdminSuccess() throws CardNotFoundException {

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
        int card3Id = card3.getCardId();

        Assoc assoc = new Assoc();
        assoc.setLcardId(card1Id);
        assoc.setRcardId(card2Id);
        assoc.setAssoc(AssocType.BLOCKS);
        assocRepository.save(assoc);

        List<Card> result = cardService.getCardAvailAssoc(card1Id, AssocType.BLOCKS);

        assertEquals(1, result.size());
        assertEquals(card3Id, result.getFirst().getCardId());
    }


    @Test
    void getCardAvailAssocMemberAccessDenied() {

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

        assertThrows(AccessDeniedException.class, () -> cardService.getCardAvailAssoc(card.getCardId(), AssocType.BLOCKS));
    }


    @Test
    void getCardAvailAssocPassive() throws CardNotFoundException {
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


        List<Card> result = cardService.getCardAvailAssoc(card1.getCardId(), AssocType.BLOCKED_BY);


        assertEquals(1, result.size());
        assertEquals(card3.getCardId(), result.getFirst().getCardId());
    }


}



