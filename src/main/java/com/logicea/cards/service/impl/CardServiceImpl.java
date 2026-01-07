package com.logicea.cards.service.impl;

import com.logicea.cards.CardNotFoundException;
import com.logicea.cards.PaginationResponse;
import com.logicea.cards.dto.CardDto;
import com.logicea.cards.entity.Card;
import com.logicea.cards.entity.User;
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

    @Override
    public List<CardDto> getAll() {
        User user = getCurrentUser();
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return cardRepository.findAll().stream()
                .filter(card -> isAdmin || card.getCreatedBy() == user.getUserId())
                .map(CardMapper::toDto)
                .collect(Collectors.toList());
    }




    @Override
    public Optional<Card> getById(int id) throws CardNotFoundException, AccessDeniedException {

        User user = getCurrentUser();
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!cardRepository.findById(id).isPresent()) {
            throw new CardNotFoundException(id);
        }
            if (isAdmin) {
                return cardRepository.findById(id);
            } else {
                Optional<Card> card = cardRepository.findById(id);
                if (card.stream().findFirst().get().getCreatedBy()== user.getUserId()) {
                    return card;
                } else {
                    throw new AccessDeniedException("You do not have permission to access this resource");
                }
            }

    }
    @Override
    public void deleteCard(int id) throws CardNotFoundException {
        if (!cardRepository.existsById(id)) { //if cardId does  not exist
            throw new CardNotFoundException(id);
        }else{
            User user = getCurrentUser(); //take the user who tries to connect
            boolean isAdmin = user.getAuthorities().stream() //take  user's role
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (isAdmin) { //if is admin then he can delete
                cardRepository.deleteById(id);
            }
            else{
                Card  card = cardRepository.findById(id).get();
                if(user.getUserId()==card.getCreatedBy()){ //check if card belongs to user
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
        if (!cardRepository.existsById(id)) { //if cardId does  not exist
            throw new CardNotFoundException(id);
        } else {
            User user = getCurrentUser();//take user
            boolean isAdmin = user.getAuthorities().stream() //take user's role
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            Card card = cardRepository.findById(id).get();
            if (isAdmin) { //if is admin he can update any card
                card.setName(newCardDto.name());
                card.setDescription(newCardDto.description());
                card.setColor(newCardDto.color());
                card.setStatus(newCardDto.status());
                card.setUpdatedBy(user.getUserId());
                card.setUpdatedAt(Instant.now());
                Card updatedCard = cardRepository.save(card);
                return CardMapper.toDto(updatedCard);
            } else { //if user is member
                if (card.getCreatedBy() == user.getUserId()) { //check if card belongs  to user
                    card.setName(newCardDto.name());
                    card.setDescription(newCardDto.description());
                    card.setColor(newCardDto.color());
                    card.setStatus(newCardDto.status());
                    card.setUpdatedBy(user.getUserId());
                    card.setUpdatedAt(Instant.now());
                    Card updatedCard = cardRepository.save(card);
                    return CardMapper.toDto(updatedCard);
                } else {
                    throw new AccessDeniedException("You do not have permission to access this resource"); //if card doesn't belong to this user
                }
            }
        }
    }
    @Override
    public CardDto partialUpdateCard(CardDto updates, int id) throws CardNotFoundException {
        if (!cardRepository.existsById(id)) { //if cardId does  not exist
            throw new CardNotFoundException(id);
        }else{ //cardId exists
            User user = getCurrentUser();//get connected user
            boolean isAdmin = user.getAuthorities().stream()//get user's role
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            Card card = cardRepository.findById(id).get(); //get card which we want to update
            if(isAdmin) { //user role is Admin
                if (updates.name() != null) card.setName(updates.name()); //finds the updated field
                if (updates.description() != null) card.setDescription(updates.description());
                if (updates.color() != null) card.setColor(updates.color());
                if (updates.status() != null) card.setStatus(updates.status());
                card.setUpdatedBy(user.getUserId());
                card.setUpdatedAt(Instant.now());
                Card updatedCard = cardRepository.save(card);
                return CardMapper.toDto(updatedCard);
            }else{
                if(card.getCreatedBy() == user.getUserId()){ //check if card belongs to user
                    if (updates.name() != null) card.setName(updates.name()); //finds the updated field
                    if (updates.description() != null) card.setDescription(updates.description());
                    if (updates.color() != null) card.setColor(updates.color());
                    if (updates.status() != null) card.setStatus(updates.status());
                    card.setUpdatedBy(user.getUserId());
                    card.setUpdatedAt(Instant.now());
                    Card updatedCard = cardRepository.save(card);
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
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sort));
        Page<Card> result = cardRepository.findAll(pageable);


        List<CardDto> dtos = result.getContent().stream()
                .map(CardMapper::toDto)
                .collect(Collectors.toList());

        return new PaginationResponse<>(page, size, sort, result.getTotalPages(), dtos);
    }
    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext(
        ).getAuthentication().getPrincipal();
    }

}
