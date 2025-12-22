package com.logicea.cards.service.impl;

import com.logicea.cards.CardNotFoundException;
import com.logicea.cards.PaginationResponse;
import com.logicea.cards.dto.CardDto;
import com.logicea.cards.entity.Card;
import com.logicea.cards.mapper.CardMapper;
import com.logicea.cards.mapper.UserDetailsMapper;
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
        UserDetailsMapper user = getCurrentUser();
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return cardRepository.findAll().stream()
                .filter(card -> isAdmin || card.getCreatedBy() == user.getUserId())
                .map(CardMapper::toDto)
                .collect(Collectors.toList());
    }




    @Override
    public Optional<Card> getById(int id) throws CardNotFoundException, AccessDeniedException {

        UserDetailsMapper user = getCurrentUser();
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if(cardRepository.findById(id).isPresent()) {
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
        else {
            throw new CardNotFoundException(id);
        }

    }



    @Override
    public CardDto newCard(CardDto cardDto) {
        System.out.println("inside newCard CardServiceImpl");
        UserDetailsMapper user = getCurrentUser();
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
    public CardDto replaceCard(CardDto newCardDto, int id) throws CardNotFoundException {
        return cardRepository.findById(id).map(card -> {
            card.setName(newCardDto.name());
            card.setDescription(newCardDto.description());
            card.setColor(newCardDto.color());
            card.setStatus(newCardDto.status());

            Card updatedCard = cardRepository.save(card);
            return CardMapper.toDto(updatedCard);
        }).orElseThrow(() -> new CardNotFoundException(id));
    }

    @Override
    public CardDto partialUpdateCard(CardDto updates, int id) throws CardNotFoundException {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));

        if (updates.name() != null) card.setName(updates.name());
        if (updates.description() != null) card.setDescription(updates.description());
        if (updates.color() != null) card.setColor(updates.color());
        if (updates.status() != null) card.setStatus(updates.status());

        Card savedCard = cardRepository.save(card);
        return CardMapper.toDto(savedCard);
    }

    @Override
    public void deleteCard(int id) throws CardNotFoundException {
        if (!cardRepository.existsById(id)) {
            throw new CardNotFoundException(id);
        }
        cardRepository.deleteById(id);
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
    private UserDetailsMapper getCurrentUser() {
        return (UserDetailsMapper) SecurityContextHolder.getContext(
        ).getAuthentication().getPrincipal();
    }

}
