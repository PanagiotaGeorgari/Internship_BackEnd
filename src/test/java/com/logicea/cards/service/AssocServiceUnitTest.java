package com.logicea.cards.service;

import com.logicea.cards.AssocAlreadyExistsException;
import com.logicea.cards.AssocNotFoundException;
import com.logicea.cards.CardNotFoundException;
import com.logicea.cards.dto.AssocDto;
import com.logicea.cards.entity.Assoc;
import com.logicea.cards.entity.Card;
import com.logicea.cards.entity.User;
import com.logicea.cards.enums.AssocType;
import com.logicea.cards.enums.UserRole;
import com.logicea.cards.repository.AssocRepository;
import com.logicea.cards.repository.CardRepository;
import com.logicea.cards.service.impl.AssocServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // activate mockito into junit

public class AssocServiceUnitTest {
    @Mock
    private AssocRepository assocRepository;
    @Mock
    private CardRepository cardRepository;
    @InjectMocks
    @Spy
    private AssocServiceImpl assocService;


    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void newAssocRightNotFound() {
        //data
        Assoc assoc = new Assoc(1, 1, AssocType.BLOCKS, 50, null);

        //action
        when(cardRepository.findById(
                assoc.getLcardId()
        )).thenReturn(
                Optional.of(new Card())
        );
        when(cardRepository.findById( // when ask you for cardId 11 (does not exist) then return empty
                assoc.getRcardId()
        )).thenReturn(
                Optional.empty()
        );

        //check
        assertThrows(CardNotFoundException.class, () -> assocService.newAssoc(assoc));

    }

    @Test
    void newAssocLeftNotFound() {
        //data
        Assoc assoc = new Assoc(1, 50, AssocType.BLOCKS, 1, null);

        //action
        when(cardRepository.findById( // when ask you for cardId 11 (does not exist) then return empty
                assoc.getLcardId()
        )).thenReturn(
                Optional.empty()
        );

        //check
        assertThrows(CardNotFoundException.class, () -> assocService.newAssoc(assoc));

    }

    @Test
    void newAssocAccessDenied() {

        //data
        Assoc assoc = new Assoc(2, 1, AssocType.BLOCKS, 2, null);

        Card lcard = new Card();
        lcard.setCardId(1);

        Card rcard = new Card();
        rcard.setCardId(2);

        when(cardRepository.findById(
                assoc.getLcardId()
        )).thenReturn(
                Optional.of(lcard)
        );

        when(cardRepository.findById(
                assoc.getRcardId()
        )).thenReturn(
                Optional.of(rcard)
        );

        doReturn(new User())
                .when(assocService).getCurrentUser();

        doReturn(false).when(assocService)
                .validateOwner(any(), any(), any());

        //check
        assertThrows(AccessDeniedException.class, () -> assocService.newAssoc(assoc));

    }

    @Test
    void newAssocAlreadyAssociated() {

        //data
        Assoc assoc = new Assoc(2, 1, AssocType.BLOCKS, 2, null);

        Card lcard = new Card();
        lcard.setCardId(1);

        Card rcard = new Card();
        rcard.setCardId(2);

        when(cardRepository.findById(
                assoc.getLcardId()
        )).thenReturn(
                Optional.of(lcard)
        );

        when(cardRepository.findById(
                assoc.getRcardId()
        )).thenReturn(
                Optional.of(rcard)
        );

        doReturn(new User())
                .when(assocService).getCurrentUser();

        doReturn(true).when(assocService)
                .validateOwner(any(), any(), any());
        doReturn(false).when(assocService)
                .uniqueAssoc(any(), any(), any());

        //check
        assertThrows(AssocAlreadyExistsException.class, () -> assocService.newAssoc(assoc));

    }

    @Test
    void newAssocSuccess() {

        //data
        Assoc assoc = new Assoc(2, 1, AssocType.BLOCKS, 2, null);

        Card lcard = new Card();
        lcard.setCardId(1);

        Card rcard = new Card();
        rcard.setCardId(2);

        when(cardRepository.findById(
                assoc.getLcardId()
        )).thenReturn(
                Optional.of(lcard)
        );

        when(cardRepository.findById(
                assoc.getRcardId()
        )).thenReturn(
                Optional.of(rcard)
        );

        doReturn(new User())
                .when(assocService).getCurrentUser();

        doReturn(true).when(assocService)
                .validateOwner(any(), any(), any());
        doReturn(true).when(assocService)
                .uniqueAssoc(any(), any(), any());

        // saved Assocs
        Assoc saved1 = new Assoc();
        saved1.setId(10);
        Assoc saved2 = new Assoc();
        saved2.setId(20);

        when(assocRepository.save(any()))
                .thenReturn(saved1).thenReturn(saved2);

        // action
        List<Integer> result = assocService.newAssoc(assoc);

        // check
        assertEquals(2, result.size());
        assertTrue(result.contains(10));
        assertTrue(result.contains(20));

    }

    @Test
    void validateOwnerAdmin() {
        User user = new User(1, "user@gmail.com", "user", UserRole.ADMIN, "pass");
        Card lcard = new Card();
        lcard.setCardId(1);
        Card rcard = new Card();
        rcard.setCardId(2);
        assertTrue(assocService.validateOwner(lcard, rcard, user));
    }

    @Test
    void validateOwnerMemberNotAccess() {
        User user = new User(1, "user@gmail.com", "user", UserRole.MEMBER, "pass");
        Card lcard = new Card();
        lcard.setCardId(1);
        lcard.setCreatedBy(1);
        Card rcard = new Card();
        rcard.setCardId(2);
        rcard.setCreatedBy(2);
        assertFalse(assocService.validateOwner(lcard, rcard, user));
    }

    @Test
    void validateOwnerMemberAccess() {
        User user = new User(1, "user@gmail.com", "user", UserRole.MEMBER, "pass");
        Card lcard = new Card();
        lcard.setCardId(1);
        lcard.setCreatedBy(1);
        Card rcard = new Card();
        rcard.setCardId(2);
        rcard.setCreatedBy(1);
        assertTrue(assocService.validateOwner(lcard, rcard, user));
    }

    @Test
    void getCurrentUserSuccess() {

        doCallRealMethod().when(assocService).getCurrentUser();

        //data
        User user = new User();
        user.setUserId(1);
        //action
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(user);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User result = assocService.getCurrentUser();
        //check
        assertEquals(user, result);
    }

    @Test
    void getCurrentUserUserNotLogIn() {
        doCallRealMethod().when(assocService).getCurrentUser();

        //action
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn("anonymousUser");

        SecurityContextHolder.getContext().setAuthentication(authentication);

        //check
        assertThrows(ClassCastException.class, () -> assocService.getCurrentUser());
    }

    @Test
    void getCardAssocs() {
        // data
        int cardId = 1;
        List<AssocDto> mockAssocs = List.of(
                new AssocDto(1, 2, 1, AssocType.BLOCKS, null),
                new AssocDto(1, 2, 3, AssocType.CHILD_OF, null)
        );

        //action
        when(assocRepository.findAssociationsAsDto(cardId)).thenReturn(mockAssocs);


        List<AssocDto> result = assocService.getCardAssocs(cardId);

        //check
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(mockAssocs, result);
    }

    @Test
    void getCardAssocsByType() {
        // arrange
        int cardId = 1;
        AssocType type = AssocType.BLOCKS;

        Collection<Assoc> mockAssocs = List.of(
                new Assoc(1, 2, type, 50, null),
                new Assoc(1, 3, type, 50, null)
        );

        //action
        when(assocRepository.findByCardIdAndAssocType(cardId, type)).thenReturn(mockAssocs);


        Collection<Assoc> result = assocService.getCardAssocsByType(cardId, type);

        //check
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(a -> a.getAssoc().equals(type)));
    }

    @Test
    void getAssocSuccess() {
        //data
        int rcardId = 1;
        int lcardId = 2;
        Assoc assoc = new Assoc(rcardId, lcardId, AssocType.BLOCKS, 50, null);
        //action
        when(assocRepository.findByRcardIdAndLcardId(rcardId, lcardId))
                .thenReturn(Optional.of(assoc));
        Assoc result = assocService.getAssoc(rcardId, lcardId);
        //check
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(2, result.getLcardId());
        assertEquals(AssocType.BLOCKS, result.getAssoc());
        assertEquals(50, result.getRcardId());

    }

    @Test
    void getAssocNotFound() {
        //data
        int rcardId = 1;
        int lcardId = 2;

        //action
        when(assocRepository.findByRcardIdAndLcardId(rcardId, lcardId))
                .thenReturn(Optional.empty());

        //check
        assertThrows(AssocNotFoundException.class, () -> assocService.getAssoc(rcardId, lcardId));


    }

    @Test
    void uniqueAssocAlreadyExist() {
        //data
        Card card1 = new Card();
        card1.setCardId(1);
        Card card2 = new Card();
        card2.setCardId(2);
        Assoc assoc = new Assoc(1, 1, AssocType.BLOCKS, 2, null);
        Assoc assoc_inv = new Assoc(2, 2, AssocType.BLOCKED_BY, 1, null);
        //action
        when(assocRepository.findByLcardIdAndRcardIdAndAssoc(1, 2, AssocType.BLOCKS))
                .thenReturn(Optional.of(assoc));
        when(assocRepository.findByLcardIdAndRcardIdAndAssoc(2, 1, AssocType.BLOCKS))
                .thenReturn(Optional.of(assoc_inv));
        boolean result = assocService.uniqueAssoc(card1, card2, AssocType.BLOCKS);
        //check
        assertFalse(result);

    }

    @Test
    void uniqueAssocSuccess() {
        //data
        Card card1 = new Card();
        card1.setCardId(1);
        Card card2 = new Card();
        card2.setCardId(2);
        //action
        when(assocRepository.findByLcardIdAndRcardIdAndAssoc(1, 2, AssocType.BLOCKS))
                .thenReturn(Optional.empty());
        when(assocRepository.findByLcardIdAndRcardIdAndAssoc(2, 1, AssocType.BLOCKS))
                .thenReturn(Optional.empty());
        boolean result = assocService.uniqueAssoc(card1, card2, AssocType.BLOCKS);
        //check
        assertTrue(result);

    }

    @Test
    void deleteAssocNotFound() {
        //data
        int assocId = 1;
        User user = new User();
        user.setUserId(1);
        user.setRole(UserRole.ADMIN);

        //action
        Authentication authentication = mock(Authentication.class); //getcurrentUser() mocked
        when(authentication.getPrincipal())
                .thenReturn(user);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        when(assocRepository.findById(
                assocId
        )).thenReturn(
                Optional.empty()
        );

        //check
        assertThrows(AssocNotFoundException.class, () -> assocService.deleteAssoc(assocId));

    }

    @Test
    void deleteAssocAdmin() {
        //data
        User user = new User();
        user.setUserId(1);
        user.setRole(UserRole.ADMIN);
        Assoc assoc = new Assoc(1, 1, AssocType.BLOCKS, 2, null);
        Assoc assocInv = new Assoc(2, 2, AssocType.BLOCKS, 1, null);


        //action
        Authentication authentication = mock(Authentication.class);// getcurrent User
        when(authentication.getPrincipal()).thenReturn(user);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);


        when(assocRepository.findById(1))
                .thenReturn(Optional.of(assoc));

        when(assocRepository.findByRcardIdAndLcardId(1, 2)) //getAssoc
                .thenReturn(Optional.of(assocInv));

        //result
        assocService.deleteAssoc(1);

    }

    @Test
    void deleteAssocMember() {
        //data
        User user = new User();
        user.setUserId(1);
        user.setRole(UserRole.MEMBER);
        Card lcard = new Card();
        lcard.setCardId(1);
        lcard.setCreatedBy(1);
        Card rcard = new Card();
        rcard.setCardId(2);
        rcard.setCreatedBy(1);
        Assoc assoc = new Assoc(1, 1, AssocType.BLOCKS, 2, null);
        Assoc assocInv = new Assoc(2, 2, AssocType.BLOCKS, 1, null);

        //action
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(user);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        when(cardRepository.findById(1))
                .thenReturn(Optional.of(lcard));
        when(cardRepository.findById(2))
                .thenReturn(Optional.of(rcard));

        when(assocRepository.findById(1))
                .thenReturn(Optional.of(assoc));
        when(assocRepository.findByRcardIdAndLcardId(1, 2)) //getAssoc
                .thenReturn(Optional.of(assocInv));


        assocService.deleteAssoc(1);
    }

    @Test
    void deleteAssocMemberNotOwner() {

        //data
        User user = new User();
        user.setUserId(1);
        user.setRole(UserRole.MEMBER);
        Card lcard = new Card();
        lcard.setCardId(1);
        lcard.setCreatedBy(2);
        Card rcard = new Card();
        rcard.setCardId(2);
        rcard.setCreatedBy(3);
        Assoc assoc = new Assoc(1, 1, AssocType.BLOCKS, 2, null);


        //action
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal())
                .thenReturn(user);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        when(assocRepository.findById(1))
                .thenReturn(Optional.of(assoc));

        //avoid UnnecessaryStubbingException when we try to run cardRepository.findById(1)
        lenient().when(cardRepository.findById(1)).thenReturn(Optional.of(lcard));
        lenient().when(cardRepository.findById(2)).thenReturn(Optional.of(rcard));

        //check
        assertThrows(AccessDeniedException.class, () -> assocService.deleteAssoc(1));
    }


}
