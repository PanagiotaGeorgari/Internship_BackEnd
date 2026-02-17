package com.logicea.cards.service.impl;

import com.logicea.cards.CardNotFoundException;
import com.logicea.cards.GetByIdResponse;
import com.logicea.cards.dto.AssocDto;
import com.logicea.cards.entity.Card;
import com.logicea.cards.entity.User;
import com.logicea.cards.enums.AssocType;
import com.logicea.cards.enums.UserRole;
import com.logicea.cards.repository.CardRepository;
import com.logicea.cards.service.AssocService;
import com.logicea.cards.service.CardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final AssocService assocService;

    public CardServiceImpl(CardRepository repository, AssocService assocService) {
        this.cardRepository = repository;
        this.assocService = assocService;
    }

    @Override
    public GetByIdResponse getById(int id) throws CardNotFoundException, AccessDeniedException {
        User user = getCurrentUser();
        boolean isAdmin = user.getRole() == UserRole.ADMIN;

        Card mainCard = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));

        if (!isAdmin) {
            assert mainCard != null;
            if (mainCard.getCreatedBy() != user.getUserId()) {
                throw new AccessDeniedException("You do not have permission to access this resource");
            }
        }
        List<AssocDto> associations = assocService.getCardAssocs(id); //sql query in cardrepository
        return new GetByIdResponse(mainCard, associations);

    }

    @Override
    public void deleteCard(int id) throws CardNotFoundException {
        Optional<Card> card = cardRepository.findById(id);
        if (card.isEmpty()) { //if cardId does  not exist
            throw new CardNotFoundException(id);
        } else {
            User user = getCurrentUser(); //take the user who tries to connect
            boolean isAdmin = user.getRole() == UserRole.ADMIN;
            if (isAdmin) { //if is admin then he can delete
                cardRepository.deleteById(id);
            } else {

                if (user.getUserId() == card.get().getCreatedBy()) { //check if card belongs to user
                    cardRepository.deleteById(id);
                } else {
                    throw new AccessDeniedException("You do not have permission to access this resource"); //error 403
                }
            }
        }

    }

    @Override
    public Card replaceCard(Card newCard, int id) throws CardNotFoundException {
        Card cardObject = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
        User user = getCurrentUser();//take user
        boolean isAdmin = user.getRole() == UserRole.ADMIN;
        if (isAdmin) { //if is admin he can update any card
            return saveCard(newCard, cardObject, user);
        } else { //if user is member
                if (cardObject.getCreatedBy() == user.getUserId()) { //check if cardObject belongs  to user
                    return saveCard(newCard, cardObject, user);
                } else {
                    throw new AccessDeniedException("You do not have permission to access this resource"); //if card doesn't belong to this user
                }
        }
    }

    private Card saveCard(Card newCard, Card cardObject, User user) {
        cardObject.setName(newCard.getName());
        cardObject.setDescription(newCard.getDescription());
        cardObject.setColor(newCard.getColor());
        cardObject.setStatus(newCard.getStatus());
        cardObject.setUpdatedBy(user.getUserId());
        cardObject.setUpdatedAt(Instant.now());
        return cardRepository.save(cardObject);
    }

    @Override
    public Card partialUpdateCard(Card updates, int id) throws CardNotFoundException {
        Card cardObject = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
        //cardId exists
        User user = getCurrentUser();//get connected user
        boolean isAdmin = user.getRole() == UserRole.ADMIN;
        if (isAdmin) { //user role is Admin
            return savePartialCard(updates, cardObject, user);
        } else {
                if (cardObject.getCreatedBy() == user.getUserId()) { //check if cardObject belongs to user
                    return savePartialCard(updates, cardObject, user);
                } else {
                    throw new AccessDeniedException("You do not have permission to access this resource"); //if card doesn't belong to this user
                }
        }
    }

    private Card savePartialCard(Card updates, Card cardObject, User user) {
        if (updates.getName() != null) cardObject.setName(updates.getName()); //finds the updated field
        if (updates.getDescription() != null) cardObject.setDescription(updates.getDescription());
        if (updates.getColor() != null) cardObject.setColor(updates.getColor());
        if (updates.getStatus() != null) cardObject.setStatus(updates.getStatus());
        cardObject.setUpdatedBy(user.getUserId());
        cardObject.setUpdatedAt(Instant.now());
        return cardRepository.save(cardObject);
    }


    public Card newCard(Card card) {
        User user = getCurrentUser();
        card.setCreatedBy(user.getUserId());
        card.setUpdatedBy(user.getUserId());
        card.setCreatedAt(Instant.now());
        card.setUpdatedAt(Instant.now());
        Card savedCard = cardRepository.save(card);
        System.out.println(savedCard.getCreatedBy());
        return savedCard;

    }


    @Override
    public Page<Card> getCardsPagination(int page, int size, String sort) {
        User user = getCurrentUser();
        boolean isAdmin = user.getRole() == UserRole.ADMIN;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sort));
        Page<Card> cardPage;
        if (isAdmin) {
            cardPage = cardRepository.findAll(pageable);
        } else {
            cardPage = cardRepository.findByCreatedBy(user.getUserId(), pageable);
        }
        return cardPage;
    }

    @Override
    public List<Card> getCardAvailAssoc(int cardId, AssocType assocType) throws CardNotFoundException {
        User user = getCurrentUser();
        boolean isAdmin = user.getRole() == UserRole.ADMIN;

        List<Card> cards = new ArrayList<>();
        if (isAdmin) {
            cards.addAll(cardRepository.findAll());
        } else {
            Card card = cardRepository.findById(cardId)
                    .orElseThrow(() -> new CardNotFoundException(cardId));

            if (card.getCreatedBy() != user.getUserId()) {
                throw new AccessDeniedException("Members can only associate their own cards!");
            }

            cards.addAll(cardRepository.findByCreatedBy(user.getUserId()));
        }

        List<Integer> removedIds;

        removedIds = assocService.getCardAssocsByType(cardId, assocType).stream()
                .map(a -> {
                    if (assocType == AssocType.BLOCKS || assocType == AssocType.CHILD_OF) {
                        return a.getRcardId();
                    } else {
                        return a.getLcardId();
                    }
                })
                .toList();

        return cards.stream()
                .filter(card -> card.getCardId() != cardId)
                .filter(card -> !removedIds.contains(card.getCardId()))
                .toList();

    }

    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext(
        ).getAuthentication().getPrincipal();
    }


}
