package com.logicea.cards.service.integration;

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
import com.logicea.cards.repository.UserRepository;
import com.logicea.cards.service.impl.AssocServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest //upload the spring
@ActiveProfiles("test")//use application-test.properties
@Transactional//clean the db after each test
public class AssocServicedb {
    @Autowired
    private AssocRepository assocRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private AssocServiceImpl assocService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Test
    void newAssocRightNotFound() {
        //data
        Assoc assoc = new Assoc();
        assoc.setLcardId(1);
        assoc.setRcardId(50);
        assoc.setAssoc(AssocType.BLOCKS);

        //check
        assertThrows(CardNotFoundException.class, () -> assocService.newAssoc(assoc));

    }

    @Test
    void newAssocLeftNotFound() {
        //data
        Assoc assoc = new Assoc();
        assoc.setLcardId(1);
        assoc.setRcardId(50);
        assoc.setAssoc(AssocType.BLOCKS);

        //check
        assertThrows(CardNotFoundException.class, () -> assocService.newAssoc(assoc));

    }

    @Test
    void newAssocAccessDenied() {

        User member = new User();
        member.setEmail("user@gmail.com");
        member.setName("user");
        member.setPassword(passwordEncoder.encode("pass"));
        member.setRole(UserRole.MEMBER);
        userRepository.save(member);


        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities())
        );


        Card lcard = new Card();
        lcard.setName("card1");
        lcard.setCreatedBy(50);
        lcard = cardRepository.save(lcard);

        Card rcard = new Card();
        rcard.setName("card2");
        rcard.setCreatedBy(51);
        rcard = cardRepository.save(rcard);

        Assoc assoc = new Assoc();
        assoc.setLcardId(lcard.getCardId());
        assoc.setRcardId(rcard.getCardId());
        assoc.setAssoc(AssocType.BLOCKS);


        assertThrows(AccessDeniedException.class, () -> assocService.newAssoc(assoc));


    }

    @Test
    void newAssocAlreadyAssociated() {

        User user = new User();
        user.setEmail("admin@gmail.com");
        user.setName("admin");
        user.setPassword("pass");
        user.setRole(UserRole.ADMIN);
        user = userRepository.save(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, List.of())
        );

        Card lcard = new Card();
        lcard.setName("card1");
        lcard = cardRepository.save(lcard);

        Card rcard = new Card();
        rcard.setName("card2");
        rcard = cardRepository.save(rcard);


        Assoc assoc = new Assoc();
        assoc.setLcardId(lcard.getCardId());
        assoc.setRcardId(rcard.getCardId());
        assoc.setAssoc(AssocType.BLOCKS);
        assocRepository.save(assoc);

        Assoc duplicate = new Assoc();
        duplicate.setLcardId(lcard.getCardId());
        duplicate.setRcardId(rcard.getCardId());
        duplicate.setAssoc(AssocType.BLOCKS);

        assertThrows(AssocAlreadyExistsException.class,() -> assocService.newAssoc(duplicate));
    }


    @Test
    void newAssocSuccess() {

        User user = new User();
        user.setEmail("user@gmail.com");
        user.setName("user");
        user.setPassword(passwordEncoder.encode("pass"));
        user.setRole(UserRole.ADMIN);
        userRepository.save(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );


        Card lcard = new Card();
        lcard.setName("card1");
        lcard = cardRepository.save(lcard);

        Card rcard = new Card();
        rcard.setName("card2");
        rcard = cardRepository.save(rcard);

        Assoc assoc = new Assoc();
        assoc.setLcardId(lcard.getCardId());
        assoc.setRcardId(rcard.getCardId());
        assoc.setAssoc(AssocType.BLOCKS);

        List<Integer> result = assocService.newAssoc(assoc);

        assertEquals(2, result.size());

    }

    @Test
    void validateOwnerAdmin() {
        User user = new User();
        user.setEmail("user@gmail.com");
        user.setRole(UserRole.ADMIN);
        user = userRepository.save(user);
        Card lcard = new Card();
        lcard.setName("card1");
        lcard = cardRepository.save(lcard);
        Card rcard = new Card();
        rcard.setName("card2");
        rcard = cardRepository.save(rcard);
        assertTrue(assocService.validateOwner(lcard, rcard, user));
    }

    @Test
    void validateOwnerMemberNotAccess() {
        User user = new User();
        user.setEmail("user@gmail.com");
        user.setName("user");
        user.setPassword(passwordEncoder.encode("pass"));
        user.setRole(UserRole.MEMBER);
        user = userRepository.save(user);

        Card lcard = new Card();
        lcard.setName("card1");
        lcard.setCreatedBy(user.getUserId());
        lcard = cardRepository.save(lcard);

        Card rcard = new Card();
        rcard.setName("card2");
        rcard.setCreatedBy(user.getUserId() + 1);
        rcard = cardRepository.save(rcard);

        assertFalse(assocService.validateOwner(lcard, rcard, user));
    }


    @Test
    void validateOwnerMemberAccess() {
        User user = new User();
        user.setEmail("user@gmail.com");
        user.setName("user");
        user.setPassword(passwordEncoder.encode("pass"));
        user.setRole(UserRole.MEMBER);
        user = userRepository.save(user);

        Card lcard = new Card();
        lcard.setName("card1");
        lcard.setCreatedBy(user.getUserId());
        lcard = cardRepository.save(lcard);
        Card rcard = new Card();
        rcard.setName("card2");
        rcard.setCreatedBy(user.getUserId());
        rcard = cardRepository.save(rcard);
        assertTrue(assocService.validateOwner(lcard, rcard, user));
    }

    @Test
    void getCurrentUserSuccess() {
        User user = new User();
        user.setEmail("user@gmail.com");
        user.setName("user");
        user.setPassword("pass");
        user.setRole(UserRole.MEMBER);
        user = userRepository.save(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, List.of())
        );

        User result = assocService.getCurrentUser();
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void getCurrentUserUserNotLogIn() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("anonymousUser", null, List.of())
        );

        assertThrows(ClassCastException.class, () -> assocService.getCurrentUser());
    }


    @Test
    void getCardAssocs() {
        // data
        Card card1 = new Card();
        card1.setName("card1");

        Card card2 = new Card();
        card2.setName("card2");

        Card card3 = new Card();
        card3.setName("card3");

        cardRepository.save(card1);
        cardRepository.save(card2);
        cardRepository.save(card3);

        Assoc assoc = new Assoc();
        assoc.setLcardId(card1.getCardId());
        assoc.setRcardId(card2.getCardId());
        assoc.setAssoc(AssocType.BLOCKS);


        Assoc assoc2 = new Assoc();
        assoc2.setLcardId(card1.getCardId());
        assoc2.setRcardId(card3.getCardId());
        assoc2.setAssoc(AssocType.CHILD_OF);

        assocRepository.save(assoc);
        assocRepository.save(assoc2);


        List<AssocDto> result = assocService.getCardAssocs(card1.getCardId());

        assertNotNull(result);
        assertEquals(2, result.size());


        assertTrue(result.stream().anyMatch(a -> a.rcardId().equals(card2.getCardId()) && a.assoc() == AssocType.BLOCKS));
        assertTrue(result.stream().anyMatch(a -> a.rcardId().equals(card3.getCardId()) && a.assoc() == AssocType.CHILD_OF));
    }

    @Test
    void getCardAssocsByType() {
        Card card1 = new Card();
        card1.setName("card1");
        Card card2 = new Card();
        card2.setName("card2");
        Card card3 = new Card();
        card3.setName("card3");

        card1 = cardRepository.save(card1);
        card2 = cardRepository.save(card2);
        card3 = cardRepository.save(card3);

        Assoc assoc = new Assoc();
        assoc.setLcardId(card1.getCardId());
        assoc.setRcardId(card2.getCardId());
        assoc.setAssoc(AssocType.BLOCKS);

        Assoc assoc2 = new Assoc();
        assoc2.setLcardId(card1.getCardId());
        assoc2.setRcardId(card3.getCardId());
        assoc2.setAssoc(AssocType.BLOCKS);

        assocRepository.save(assoc);
        assocRepository.save(assoc2);

        Collection<Assoc> result = assocService.getCardAssocsByType(card1.getCardId(), AssocType.BLOCKS);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(a -> a.getAssoc().equals(AssocType.BLOCKS)));
    }

    @Test
    void getAssocSuccess() {

        Card card1 = new Card();
        card1.setName("card1");
        Card card2 = new Card();
        card2.setName("card2");
        card1 = cardRepository.save(card1);
        card2 = cardRepository.save(card2);

        Assoc assoc = new Assoc();
        assoc.setLcardId(card1.getCardId());
        assoc.setRcardId(card2.getCardId());
        assoc.setAssoc(AssocType.BLOCKS);
        assoc = assocRepository.save(assoc);

        Assoc result = assocService.getAssoc(card2.getCardId(), card1.getCardId());

        assertNotNull(result);
        assertEquals(assoc.getId(), result.getId());
        assertEquals(card1.getCardId(), result.getLcardId());
        assertEquals(card2.getCardId(), result.getRcardId());
        assertEquals(AssocType.BLOCKS, result.getAssoc());
    }

    @Test
    void getAssocNotFound() {

        Card card1 = new Card();
        card1.setName("card1");
        Card card2 = new Card();
        card2.setName("card2");
        cardRepository.save(card1);
        cardRepository.save(card2);

        assertThrows(AssocNotFoundException.class, () -> assocService.getAssoc(card1.getCardId(), card2.getCardId()));

    }

    @Test
    void uniqueAssocAlreadyExist() {
        //data
        Card card1 = new Card();
        card1.setName("card1");
        Card card2 = new Card();
        card2.setName("card2");
        card1 = cardRepository.save(card1);
        card2 = cardRepository.save(card2);

        Assoc assoc1 = new Assoc();
        assoc1.setLcardId(card1.getCardId());
        assoc1.setRcardId(card2.getCardId());
        assoc1.setAssoc(AssocType.BLOCKS);
        assocRepository.save(assoc1);

        Assoc assoc2 = new Assoc();
        assoc2.setLcardId(card2.getCardId());
        assoc2.setRcardId(card1.getCardId());
        assoc2.setAssoc(AssocType.BLOCKED_BY);
        assocRepository.save(assoc2);


        boolean result = assocService.uniqueAssoc(card1, card2, AssocType.BLOCKS);
        //check
        assertFalse(result);

    }

    @Test
    void uniqueAssocSuccess() {
        //data
        Card card1 = new Card();
        card1.setName("card1");
        card1 = cardRepository.save(card1);
        Card card2 = new Card();
        card2.setName("card2");
        card2 = cardRepository.save(card2);

        boolean result = assocService.uniqueAssoc(card1, card2, AssocType.BLOCKS);
        //check
        assertTrue(result);

    }

    @Test
    void deleteAssocNotFound() {

        User user = new User();
        user.setPassword("password");
        user.setName("user");
        user.setEmail("email@gmail.com");
        user.setRole(UserRole.ADMIN);


        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );

        assertThrows(AssocNotFoundException.class, () -> assocService.deleteAssoc(50));

    }

    @Test
    void deleteAssocAdmin() {
        //data
        User user = new User();
        user.setEmail("user@gmail.com");
        user.setName("user");
        user.setPassword("password");
        user.setRole(UserRole.ADMIN);

        Card card1 = new Card();
        card1.setName("card1");
        card1 = cardRepository.save(card1);
        Card card2 = new Card();
        card2.setName("card2");

        Assoc assoc = new Assoc();
        assoc.setLcardId(card1.getCardId());
        assoc.setRcardId(card2.getCardId());
        assoc.setAssoc(AssocType.BLOCKS);
        assoc = assocRepository.save(assoc);

        Assoc assoc2 = new Assoc();
        assoc2.setLcardId(card2.getCardId());
        assoc2.setRcardId(card1.getCardId());
        assoc2.setAssoc(AssocType.BLOCKS);
        assocRepository.save(assoc2);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );

        assocService.deleteAssoc(assoc.getId());

    }

    @Test
    void deleteAssocMember() {
        //data
        User user = new User();
        user.setName("user");
        user.setEmail("email@gmail.com");
        user.setPassword("password");
        user.setRole(UserRole.MEMBER);

        Card lcard = new Card();
        lcard.setName("lcard");
        lcard.setCreatedBy(user.getUserId());
        cardRepository.save(lcard);

        Card rcard = new Card();
        rcard.setName("rcard");
        rcard.setCreatedBy(user.getUserId());
        cardRepository.save(rcard);

        Assoc assoc = new Assoc();
        assoc.setLcardId(lcard.getCardId());
        assoc.setRcardId(rcard.getCardId());
        assoc.setAssoc(AssocType.BLOCKS);
        assoc = assocRepository.save(assoc);

        Assoc assoc2 = new Assoc();
        assoc2.setLcardId(rcard.getCardId());
        assoc2.setRcardId(lcard.getCardId());
        assoc2.setAssoc(AssocType.BLOCKS);
        assocRepository.save(assoc2);


        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );

        assocService.deleteAssoc(assoc.getId());
    }




}
