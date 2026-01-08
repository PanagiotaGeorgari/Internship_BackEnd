package com.logicea.cards.service.impl;

import com.logicea.cards.CardNotFoundException;
import com.logicea.cards.PaginationResponse;
import com.logicea.cards.dto.CardDto;
import com.logicea.cards.entity.Card;
import com.logicea.cards.entity.User;
import com.logicea.cards.enums.UserRole;
import com.logicea.cards.mapper.CardMapper;
import com.logicea.cards.repository.CardRepository;
import com.logicea.cards.service.CardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CardServiceImpl implements CardService  {

    private final CardRepository cardRepository;

    public CardServiceImpl(CardRepository repository) {
        this.cardRepository = repository;
    }

    /*@Override
    public List<CardDto> getAll() {
        User user = getCurrentUser();
        boolean isAdmin = user.getRole() == UserRole.ADMIN;
        List<Card> cards;
        if(isAdmin){
            cards = cardRepository.findAll();
        }else{
           cards= cardRepository.findByCreatedBy(user.getUserId());
        }
        return cards.stream()
                .map(CardMapper::toDto)
                .collect(Collectors.toList());

    }*/




    @Override
    public Optional<Card> getById(int id) throws CardNotFoundException, AccessDeniedException {

        User user = getCurrentUser();
        boolean isAdmin = user.getRole() == UserRole.ADMIN;
        Optional<Card> card =cardRepository.findById(id);
        if (!card.isPresent()) {
            throw new CardNotFoundException(id);
        }
            if (isAdmin) {
                return card;
            } else {

                if (card.get().getCreatedBy()== user.getUserId()) {
                    return card;
                } else {
                    throw new AccessDeniedException("You do not have permission to access this resource");
                }
            }

    }
    @Override
    public void deleteCard(int id) throws CardNotFoundException {
        Optional<Card> card = cardRepository.findById(id);
        if (card.isEmpty()) { //if cardId does  not exist
            throw new CardNotFoundException(id);
        }else{
            User user = getCurrentUser(); //take the user who tries to connect
            boolean isAdmin = user.getRole() == UserRole.ADMIN;
            if (isAdmin) { //if is admin then he can delete
                cardRepository.deleteById(id);
            }
            else{

                if(user.getUserId()==card.get().getCreatedBy()){ //check if card belongs to user
                    cardRepository.deleteById(id);
                }
                else{
                    throw new AccessDeniedException("You do not have permission to access this resource"); //error 403
                }
            }
        }

    }

    @Override
    public CardDto replaceCard(CardDto newCardDto, int id) throws CardNotFoundException {
        Optional<Card> card = cardRepository.findById(id);
        Card cardObject = card.get();
        if (card.isEmpty()) { //if cardId does  not exist
            throw new CardNotFoundException(id);
        } else {
            User user = getCurrentUser();//take user
            boolean isAdmin = user.getRole() == UserRole.ADMIN;
            if (isAdmin) { //if is admin he can update any card
                cardObject.setName(newCardDto.name());
                cardObject.setDescription(newCardDto.description());
                cardObject.setColor(newCardDto.color());
                cardObject.setStatus(newCardDto.status());
                cardObject.setUpdatedBy(user.getUserId());
                cardObject.setUpdatedAt(Instant.now());
                Card updatedCard = cardRepository.save(cardObject);
                return CardMapper.toDto(updatedCard);
            } else { //if user is member
                if (cardObject.getCreatedBy() == user.getUserId()) { //check if cardObject belongs  to user
                    cardObject.setName(newCardDto.name());
                    cardObject.setDescription(newCardDto.description());
                    cardObject.setColor(newCardDto.color());
                    cardObject.setStatus(newCardDto.status());
                    cardObject.setUpdatedBy(user.getUserId());
                    cardObject.setUpdatedAt(Instant.now());
                    Card updatedCard = cardRepository.save(cardObject);
                    return CardMapper.toDto(updatedCard);
                } else {
                    throw new AccessDeniedException("You do not have permission to access this resource"); //if card doesn't belong to this user
                }
            }
        }
    }
    @Override
    public CardDto partialUpdateCard(CardDto updates, int id) throws CardNotFoundException {
        Optional<Card> card = cardRepository.findById(id);
        Card cardObject = card.get();

        if (card.isEmpty()) { //if cardId does  not exist
            throw new CardNotFoundException(id);
        }else{ //cardId exists
            User user = getCurrentUser();//get connected user
            boolean isAdmin = user.getRole() == UserRole.ADMIN;
            if(isAdmin) { //user role is Admin
                if (updates.name() != null) cardObject.setName(updates.name()); //finds the updated field
                if (updates.description() != null) cardObject.setDescription(updates.description());
                if (updates.color() != null) cardObject.setColor(updates.color());
                if (updates.status() != null) cardObject.setStatus(updates.status());
                cardObject.setUpdatedBy(user.getUserId());
                cardObject.setUpdatedAt(Instant.now());
                Card updatedCard = cardRepository.save(cardObject);
                return CardMapper.toDto(updatedCard);
            }else{
                if(cardObject.getCreatedBy() == user.getUserId()){ //check if cardObject belongs to user
                    if (updates.name() != null) cardObject.setName(updates.name()); //finds the updated field
                    if (updates.description() != null) cardObject.setDescription(updates.description());
                    if (updates.color() != null) cardObject.setColor(updates.color());
                    if (updates.status() != null) cardObject.setStatus(updates.status());
                    cardObject.setUpdatedBy(user.getUserId());
                    cardObject.setUpdatedAt(Instant.now());
                    Card updatedCard = cardRepository.save(cardObject);
                    return CardMapper.toDto(updatedCard);
                }else{
                    throw new AccessDeniedException("You do not have permission to access this resource"); //if card doesn't belong to this user
                }
            }
        }
    }



    @Override
    public CardDto newCard(CardDto cardDto) {
        User user = getCurrentUser();
        int userId = user.getUserId();
        Card card = CardMapper.toEntity(cardDto);
        card.setCreatedBy(user.getUserId());
        card.setUpdatedBy(user.getUserId());
        card.setCreatedAt( Instant.now());
        card.setUpdatedAt( Instant.now());
        Card savedCard = cardRepository.save(card);
        System.out.println(savedCard.getCreatedBy());
        return CardMapper.toDto(savedCard);

    }




    @Override
    public PaginationResponse<CardDto> getCardsPagination(int page, int size, String sort) {
        User user = getCurrentUser();
        boolean isAdmin = user.getRole() == UserRole.ADMIN;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sort));
        Page<Card> cardPage;
        if (isAdmin) {
            cardPage = cardRepository.findAll(pageable);
        }else{
            cardPage = cardRepository.findByCreatedBy(user.getUserId(), pageable);
        }


        List<CardDto> dtos = cardPage.getContent().stream()
                .map(CardMapper::toDto)
                .collect(Collectors.toList());

        return new PaginationResponse<>(page, size, sort, cardPage.getTotalPages(), dtos);
    }
    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext(
        ).getAuthentication().getPrincipal();
    }

}
