package com.logicea.cards.service.impl;

import com.logicea.cards.CardNotFoundException;
import com.logicea.cards.PaginationResponse;
import com.logicea.cards.dto.CardDto;
import com.logicea.cards.entity.Card;
import com.logicea.cards.mapper.CardMapper;
import com.logicea.cards.repository.CardRepository;
import com.logicea.cards.service.CardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    public CardServiceImpl(CardRepository repository) {
        this.cardRepository = repository;
    }
    @Override
    public List<CardDto> getAll() {
        return cardRepository.findAll().stream()
                .map(CardMapper::toDto)
                .collect(Collectors.toList());
    }



    @Override
    public CardDto getById(int id) throws CardNotFoundException {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
        return CardMapper.toDto(card);
    }



    @Override
    public CardDto newCard(CardDto cardDto) {
        Card card = CardMapper.toEntity(cardDto);
        Card savedCard = cardRepository.save(card);
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
}
