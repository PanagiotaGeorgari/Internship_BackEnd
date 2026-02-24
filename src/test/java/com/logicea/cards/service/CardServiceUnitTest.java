package com.logicea.cards.service;

import com.logicea.cards.CardNotFoundException;
import com.logicea.cards.GetByIdResponse;
import com.logicea.cards.dto.AssocDto;
import com.logicea.cards.entity.Assoc;
import com.logicea.cards.entity.Card;
import com.logicea.cards.entity.User;
import com.logicea.cards.enums.AssocType;
import com.logicea.cards.enums.CardStatus;
import com.logicea.cards.enums.UserRole;
import com.logicea.cards.repository.CardRepository;
import com.logicea.cards.service.impl.CardServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // activate mockito into junit
public class CardServiceUnitTest {
    //same fields with cardServiceImpl
    @Mock //dummy
    private CardRepository cardRepository;
    @Mock
    private AssocService assocService;
    @InjectMocks // create an object cardserviceimpl and add the mocks
    @Spy // we mocked only the method getcurrentuser
    private CardServiceImpl cardService; //in order to take getCurrentUser()
    private User currentUser;

    private Card mockCard;//create a mock card

    @BeforeEach
    void setUp() {
        //create a fake user and card
        currentUser = new User(1, "utest@gmail.com", "userTest", UserRole.MEMBER, "passtest");

        mockCard = new Card();
        mockCard.setCardId(10);
        mockCard.setName("cardTest");
        mockCard.setDescription("new card for test mock");
        mockCard.setColor("#abc123");
        mockCard.setCreatedBy(1);

        //when i want to take the current user return the user i made in setup (fake - mocked)
        lenient().doReturn(currentUser)
                .when(cardService)
                .getCurrentUser();
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    @Test
    void getByIdSuccessAdmin() {
        //data
        int cardId = 10;
        currentUser.setRole(UserRole.ADMIN);
        mockCard.setCreatedBy(currentUser.getUserId());// card belongs to mockeduser

        List<AssocDto> mockAssocs = new ArrayList<>();
        when(cardRepository.findById( //when ask you for cardId 10 return mockCard
                cardId
        )).thenReturn(
                Optional.of(mockCard)
        );

        when(assocService.getCardAssocs(// when ask you for assocs for cardid 10 return  mockAssocs
                cardId
        )).thenReturn(
                mockAssocs
        );

        //response
        // call the same method from the cardserviceimpl
        GetByIdResponse response = cardService.getById(cardId);

        //ckeck
        assertNotNull(response);
        assertEquals(mockCard, response.getCard());
        assertEquals(mockAssocs, response.getAssocs());

    }

    @Test
    void getByIdSuccessMember() {
        //data
        int cardId = 10;
        currentUser.setRole(UserRole.MEMBER);
        mockCard.setCreatedBy(currentUser.getUserId());// card belongs to mockeduser

        List<AssocDto> mockAssocs = new ArrayList<>();
        when(cardRepository.findById( //when ask you for cardId 10 return mockCard
                cardId
        )).thenReturn(
                Optional.of(mockCard)
        );

        when(assocService.getCardAssocs(// when ask you for assocs for cardid 10 return  mockAssocs
                cardId
        )).thenReturn(
                mockAssocs
        );

        //response
        // call the same method from the cardserviceimpl
        GetByIdResponse response = cardService.getById(cardId);

        //ckeck
        assertNotNull(response);
        assertEquals(mockCard, response.getCard());
        assertEquals(mockAssocs, response.getAssocs());

    }

    @Test
    void getByIdCardNotFound() throws CardNotFoundException {
        //data
        int cardId = 11;

        //action-response
        when(cardRepository.findById( // when ask you for cardId 11 (does not exist) then return empty
                cardId
        )).thenReturn(
                Optional.empty()
        );

        //ckeck
        assertThrows(CardNotFoundException.class, () -> cardService.getById(cardId));

    }

    @Test
    void getByIdMemberAccessDenied() throws AccessDeniedException {
        //data
        int cardId = 10;
        currentUser.setRole(UserRole.MEMBER);
        mockCard.setCreatedBy(5);

        //action
        when(cardRepository.findById(
                cardId
        )).thenReturn(
                Optional.of(mockCard)
        );
        //check
        assertThrows(AccessDeniedException.class, () -> cardService.getById(cardId));
    }

    @Test
    void deleteCardNotFound() throws CardNotFoundException {
        //data
        int cardId = 55;
        //action
        when(cardRepository.findById(
                cardId
        )).thenReturn(
                Optional.empty()
        );
        //check
        assertThrows(CardNotFoundException.class, () -> cardService.deleteCard(cardId));
    }

    @Test
    void deleteCardSuccessMember() {
        //data
        int cardId = 10;
        mockCard.setCreatedBy(currentUser.getUserId());
        currentUser.setRole(UserRole.MEMBER);
        //action
        when(cardRepository.findById( //when ask you for cardId 10 return mockCard
                cardId
        )).thenReturn(
                Optional.of(mockCard)
        );
        //check
        assertDoesNotThrow(() -> cardService.deleteCard(cardId));

    }

    @Test
    void deleteCardSuccessAdmin() {
        //data
        int cardId = 10;
        mockCard.setCreatedBy(currentUser.getUserId());
        currentUser.setRole(UserRole.ADMIN);
        //action
        when(cardRepository.findById(
                cardId
        )).thenReturn(
                Optional.of(mockCard)
        );
        //check
        assertDoesNotThrow(() -> cardService.deleteCard(cardId));

    }

    @Test
    void deleteCardMemberNotAccess() {
        //data
        int cardId = 18;
        mockCard.setCreatedBy(20);
        //action
        when(cardRepository.findById(
                cardId
        )).thenReturn(
                Optional.of(mockCard)
        );
        //check
        assertThrows(AccessDeniedException.class, () -> cardService.deleteCard(cardId));
    }

    @Test
    void replaceNotFound() {
        //data
        int cardId = 90;
        //action
        when(cardRepository.findById(
                cardId
        )).thenReturn(
                Optional.empty()
        );
        //check
        assertThrows(CardNotFoundException.class, () -> cardService.replaceCard(mockCard, cardId));
    }

    @Test
    void replaceCardSuccessAdmin() {
        //data
        int cardId = 10;

        Card newCard = new Card();
        newCard.setCardId(cardId);
        newCard.setName("newCard");
        newCard.setDescription("new card for test update admin");
        newCard.setColor("#abc123");
        newCard.setCreatedBy(currentUser.getUserId());

        currentUser.setRole(UserRole.ADMIN);

        //action
        when(cardRepository.findById(
                cardId
        )).thenReturn(
                Optional.of(mockCard)
        );
        when(cardRepository.save(any(
                Card.class
        ))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );


        Card result = cardService.replaceCard(newCard, cardId);

        //check
        assertEquals("newCard", result.getName());
        assertEquals("new card for test update admin", result.getDescription());
        assertEquals("#abc123", result.getColor());
        assertEquals(1, result.getUpdatedBy());

    }

    @Test
    void replaceCardSuccessMember() {
        //data
        int cardId = 10;
        currentUser.setRole(UserRole.MEMBER);
        mockCard.setCreatedBy(currentUser.getUserId());
        Card newCard = new Card();
        newCard.setCardId(cardId);
        newCard.setName("newCard");
        newCard.setDescription("new card for test update member");
        newCard.setColor("#abc123");
        newCard.setCreatedBy(currentUser.getUserId());

        //action
        when(cardRepository.findById(
                cardId
        )).thenReturn(
                Optional.of(mockCard)
        );
        when(cardRepository.save(any(
                Card.class
        ))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );


        Card result = cardService.replaceCard(newCard, cardId);

        //check
        assertEquals("newCard", result.getName());
        assertEquals("new card for test update member", result.getDescription());
        assertEquals("#abc123", result.getColor());
        assertEquals(1, result.getUpdatedBy());

    }

    @Test
    void replaceCardMemberNotAccess() {
        //data
        int cardId = 10;
        currentUser.setRole(UserRole.MEMBER);
        mockCard.setCreatedBy(8);
        //action
        when(cardRepository.findById(
                cardId
        )).thenReturn(
                Optional.of(mockCard)
        );

        //check
        assertThrows(AccessDeniedException.class, () -> cardService.replaceCard(mockCard, cardId));
    }

    @Test
    void partialUpdateCardNotFound() {
        //data
        int cardId = 90;
        //action
        when(cardRepository.findById(
                cardId
        )).thenReturn(
                Optional.empty()
        );
        //check
        assertThrows(CardNotFoundException.class, () -> cardService.partialUpdateCard(mockCard, cardId));

    }

    @Test
    void partialUpdateCardSuccessAdmin() {
        //data
        int cardId = 10;
        currentUser.setRole(UserRole.ADMIN);
        mockCard.setCreatedBy(currentUser.getUserId());
        Card newCard = new Card();
        newCard.setCardId(cardId);
        newCard.setName("updated field");
        newCard.setDescription("updatedDescription");
        newCard.setColor("#fff111");
        newCard.setStatus(CardStatus.DONE);


        //action
        when(cardRepository.findById(
                cardId
        )).thenReturn(
                Optional.of(mockCard)
        );
        when(cardRepository.save(any(
                Card.class
        ))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );


        Card result = cardService.partialUpdateCard(newCard, cardId);

        //check
        assertEquals("updated field", result.getName());
        assertEquals("updatedDescription", result.getDescription());
        assertEquals("#fff111", result.getColor());
        assertEquals(CardStatus.DONE, result.getStatus());
        assertEquals(1, result.getUpdatedBy());


    }

    @Test
    void partialUpdateCardSuccessMember() {
        //data
        int cardId = 10;
        currentUser.setRole(UserRole.MEMBER);
        mockCard.setCreatedBy(currentUser.getUserId());
        Card newCard = new Card();
        newCard.setColor("#abc123");
        newCard.setName("updated field");
        newCard.setDescription("updatedDescription");
        newCard.setStatus(CardStatus.DONE);


        //action
        when(cardRepository.findById(
                cardId
        )).thenReturn(
                Optional.of(mockCard)
        );
        when(cardRepository.save(any(
                Card.class
        ))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );

        Card result = cardService.partialUpdateCard(newCard, cardId);

        //check
        assertEquals("#abc123", result.getColor());
        assertEquals(1, result.getUpdatedBy());
        assertEquals("updated field", result.getName());
        assertEquals("updatedDescription", result.getDescription());
        assertEquals(CardStatus.DONE, result.getStatus());


    }


    @Test
    void partialUpdateCardMemberDeniedAccess() {
        //data
        int cardId = 10;
        currentUser.setRole(UserRole.MEMBER);
        mockCard.setCreatedBy(8);
        //action
        when(cardRepository.findById(
                cardId
        )).thenReturn(
                Optional.of(mockCard)
        );

        //check
        assertThrows(AccessDeniedException.class, () -> cardService.partialUpdateCard(mockCard, cardId));

    }

    @Test
    void newCardSuccess() {
        Card newCard = new Card();
        newCard.setCardId(100);
        newCard.setName("new card name");
        newCard.setDescription("new card");
        newCard.setColor("#abc123");
        newCard.setCreatedBy(currentUser.getUserId());

        //action
        when(cardRepository.save(any(
                Card.class
        ))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );

        Card result = cardService.newCard(newCard);

        //check
        assertEquals("new card name", result.getName());
        assertEquals("new card", result.getDescription());
        assertEquals("#abc123", result.getColor());
        assertEquals(1, result.getUpdatedBy());

    }

    @Test
    void getCurrentUserSuccess() {

        doCallRealMethod().when(cardService).getCurrentUser();

        //data
        User user = new User();
        user.setUserId(1);
        //action
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(user);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User result = cardService.getCurrentUser();
        //check
        assertEquals(user, result);
    }

    @Test
    void getCurrentUserUserNotLogIn() {
        doCallRealMethod().when(cardService).getCurrentUser();

        //action
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn("anonymousUser");

        SecurityContextHolder.getContext().setAuthentication(authentication);

        //check
        assertThrows(ClassCastException.class, () -> cardService.getCurrentUser());
    }

    @Test
    void getCardsPaginationAdminSuccess() {

        doCallRealMethod().when(cardService).getCardsPagination(anyInt(), anyInt(), anyString());
        //data
        currentUser.setRole(UserRole.ADMIN);
        Card card1 = new Card();
        card1.setCardId(1);
        card1.setName("card1");
        card1.setDescription("card1");
        card1.setColor("#abc123");
        card1.setCreatedBy(currentUser.getUserId());
        Card card2 = new Card();
        card2.setCardId(2);
        card2.setName("card2");
        card2.setDescription("card2");
        card2.setColor("#abc123");
        card2.setCreatedBy(currentUser.getUserId());

        //actiom
        Page<Card> mockPage = new PageImpl<>(List.of(card1, card2));

        when(cardRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        Page<Card> result = cardService.getCardsPagination(0, 10, "cardId");

        //check
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().contains(card1));
        assertTrue(result.getContent().contains(card2));
    }

    @Test
    void getCardsPaginationMemberSuccess() {

        doCallRealMethod().when(cardService).getCardsPagination(anyInt(), anyInt(), anyString());

        //data
        Card card1 = new Card();
        card1.setCardId(1);
        card1.setName("card1");
        card1.setDescription("card1");
        card1.setColor("#abc123");
        card1.setCreatedBy(currentUser.getUserId());
        Card card2 = new Card();
        card2.setCardId(2);
        card2.setName("card2");
        card2.setDescription("card2");
        card2.setColor("#abc123");
        card2.setCreatedBy(currentUser.getUserId());

        //action
        Page<Card> mockPage = new PageImpl<>(List.of(card1, card2));

        when(cardRepository.findByCreatedBy(eq(1), any(Pageable.class))).thenReturn(mockPage);

        Page<Card> result = cardService.getCardsPagination(0, 10, "cardId");

        //check
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().contains(card1));
        assertTrue(result.getContent().contains(card2));
    }

    @Test
    void getCardAvailAssocAdminSuccess() {

        doCallRealMethod().when(cardService).getCardAvailAssoc(anyInt(), any(AssocType.class));

        //data
        currentUser.setRole(UserRole.ADMIN);
        Card card1 = new Card();
        card1.setCardId(1);
        card1.setName("card1");
        card1.setDescription("card1");
        card1.setColor("#abc123");
        card1.setCreatedBy(currentUser.getUserId());
        Card card2 = new Card();
        card2.setCardId(2);
        card2.setName("card2");
        card2.setDescription("card2");
        card2.setColor("#abc123");
        card2.setCreatedBy(currentUser.getUserId());
        Card card3 = new Card();
        card3.setCardId(3);
        card3.setName("card3");
        card3.setDescription("card3");
        card3.setColor("#abc123");
        card3.setCreatedBy(currentUser.getUserId());

        Assoc assoc = new Assoc(1, 1, AssocType.BLOCKS, 2, null);


        //action
        when(cardRepository.findAll())
                .thenReturn(List.of(card1, card2, card3));


        when(assocService.getCardAssocsByType(1, AssocType.BLOCKS))
                .thenReturn(List.of(assoc));

        List<Card> result = cardService.getCardAvailAssoc(1, AssocType.BLOCKS);

        //check
        assertEquals(1, result.size());
        assertEquals(3, result.getFirst().getCardId());
    }

    @Test
    void getCardAvailAssocMemberSuccess() {

        doCallRealMethod().when(cardService).getCardAvailAssoc(anyInt(), any(AssocType.class));

        //data
        currentUser.setRole(UserRole.MEMBER);
        Card card1 = new Card();
        card1.setCardId(1);
        card1.setName("card1");
        card1.setDescription("card1");
        card1.setColor("#abc123");
        card1.setCreatedBy(currentUser.getUserId());

        Card card2 = new Card();
        card2.setCardId(2);
        card2.setName("card2");
        card2.setDescription("card2");
        card2.setColor("#abc123");
        card2.setCreatedBy(currentUser.getUserId());

        Card card3 = new Card();
        card3.setCardId(3);
        card3.setName("card3");
        card3.setDescription("card3");
        card3.setColor("#abc123");
        card3.setCreatedBy(currentUser.getUserId());

        Assoc assoc = new Assoc(1, 1, AssocType.BLOCKS, 2, null);

        //action
        when(cardRepository.findById(1)).thenReturn(Optional.of(card1));
        when(cardRepository.findByCreatedBy(currentUser.getUserId()))
                .thenReturn(List.of(card1, card2, card3));
        when(assocService.getCardAssocsByType(1, AssocType.BLOCKS))
                .thenReturn(List.of(assoc));

        List<Card> result = cardService.getCardAvailAssoc(1, AssocType.BLOCKS);
        //check
        assertEquals(1, result.size());
        assertEquals(3, result.getFirst().getCardId());

    }

    @Test
    void getCardAvailAssocMemberSuccessPassive() {

        doCallRealMethod().when(cardService).getCardAvailAssoc(anyInt(), any(AssocType.class));

        //data
        currentUser.setRole(UserRole.MEMBER);
        Card card1 = new Card();
        card1.setCardId(1);
        card1.setName("card1");
        card1.setDescription("card1");
        card1.setColor("#abc123");
        card1.setCreatedBy(currentUser.getUserId());

        Card card2 = new Card();
        card2.setCardId(2);
        card2.setName("card2");
        card2.setDescription("card2");
        card2.setColor("#abc123");
        card2.setCreatedBy(currentUser.getUserId());

        Card card3 = new Card();
        card3.setCardId(3);
        card3.setName("card3");
        card3.setDescription("card3");
        card3.setColor("#abc123");
        card3.setCreatedBy(currentUser.getUserId());

        Assoc assoc = new Assoc(1, 1, AssocType.BLOCKED_BY, 2, null);

        //action
        when(cardRepository.findById(1)).thenReturn(Optional.of(card1));
        when(cardRepository.findByCreatedBy(currentUser.getUserId()))
                .thenReturn(List.of(card1, card2, card3));
        when(assocService.getCardAssocsByType(1, AssocType.BLOCKS))
                .thenReturn(List.of(assoc));

        List<Card> result = cardService.getCardAvailAssoc(1, AssocType.BLOCKS);
        //check
        assertEquals(1, result.size());
        assertEquals(3, result.getFirst().getCardId());

    }

    @Test
    void getCardAvailAssocMemberAccessDenied() {

        doCallRealMethod().when(cardService).getCardAvailAssoc(anyInt(), any(AssocType.class));

        //data
        Card card = new Card();
        card.setCardId(1);
        card.setName("card1");
        card.setDescription("card1");
        card.setColor("#abc123");
        card.setCreatedBy(8);

        //action
        when(cardRepository.findById(1))
                .thenReturn(Optional.of(card));

        // check
        assertThrows(AccessDeniedException.class, () -> cardService.getCardAvailAssoc(1, AssocType.BLOCKS));
    }

}
