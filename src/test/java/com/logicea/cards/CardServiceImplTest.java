
package com.logicea.cards;

import com.logicea.cards.dto.AssocDto;
import com.logicea.cards.entity.Card;
import com.logicea.cards.entity.User;
import com.logicea.cards.enums.CardStatus;
import com.logicea.cards.enums.UserRole;
import com.logicea.cards.repository.CardRepository;
import com.logicea.cards.service.AssocService;
import com.logicea.cards.service.impl.CardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // activate mockito into junit
public class CardServiceImplTest {
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
        currentUser = new User(1,"utest@gmail.com","userTest",UserRole.MEMBER,"passtest");

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

    @Test
    void getByIdSuccess() throws CardNotFoundException {
        int cardId = 10;
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

        // call the same method from the cardserviceimpl
        GetByIdResponse response = cardService.getById(cardId);


        assertNotNull(response);
        assertEquals(mockCard, response.getCard());
        assertEquals(mockAssocs, response.getAssocs());

    }

    @Test
    void getByIdCardNotFound() throws CardNotFoundException {
        int cardId = 11;
        mockCard.setCreatedBy(currentUser.getUserId());

        when(cardRepository.findById( // when ask you for cardId 11 (does not exist) then return empty
                cardId
        )).thenReturn(
                Optional.empty()
        );

        assertThrows(CardNotFoundException.class, () -> cardService.getById(cardId));

    }

    @Test
    void getByIdMemberAccessDenied() throws AccessDeniedException {
        int cardId = 10; //chαnge the card's 10 creator from 10 -> 5
        mockCard.setCreatedBy(5);

        when(cardRepository.findById( // when ask you for card id 10 return empty
                cardId
        )).thenReturn(
                Optional.of(mockCard)
        );

        assertThrows(AccessDeniedException.class, () -> cardService.getById(cardId));
    }

    @Test
    void deleteCardNotFound() throws CardNotFoundException {
        int cardId = 55;
        when(cardRepository.findById(
                cardId
        )).thenReturn(
                Optional.empty()
        );
        assertThrows(CardNotFoundException.class, () -> cardService.deleteCard(cardId));
    }

    @Test
    void deleteCardSuccess(){
        int cardId = 10;
        mockCard.setCreatedBy(currentUser.getUserId());
        when(cardRepository.findById( //when ask you for cardId 10 return mockCard
                cardId
        )).thenReturn(
                Optional.of(mockCard)
        );
        assertDoesNotThrow(() -> cardService.deleteCard(cardId));

    }

}

