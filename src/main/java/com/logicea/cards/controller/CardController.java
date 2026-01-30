package com.logicea.cards.controller;

import com.logicea.cards.CardNotFoundException;
import com.logicea.cards.GetAvailResponce;
import com.logicea.cards.GetByIdResponse;
import com.logicea.cards.PaginationResponse;
import com.logicea.cards.dto.CardDto;
import com.logicea.cards.dto.CardSummaryDto;
import com.logicea.cards.entity.Card;
import com.logicea.cards.enums.AssocType;
import com.logicea.cards.mapper.CardMapper;
import com.logicea.cards.service.CardService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;


    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")
    public PaginationResponse<CardSummaryDto> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "cardId") String sort) {
        Page<Card> cardPage = cardService.getCardsPagination(page, size, sort);
        List<CardSummaryDto> dtos = cardPage.getContent().stream()
                .map(card -> new CardSummaryDto(card.getCardId(), card.getName()))
                .toList();
        return new PaginationResponse<>(
                cardPage.getNumber(),
                cardPage.getSize(),
                sort,
                cardPage.getTotalPages(),
                dtos
        );
    }

    @GetMapping("/{cardId}")
    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")
    public GetByIdResponse getById(@PathVariable int cardId) throws CardNotFoundException, AccessDeniedException {
        return cardService.getById(cardId);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")
    public CardDto newCard(@Valid @RequestBody CardDto newCardDto) {
        Card card = CardMapper.toEntity(newCardDto);
        card = cardService.newCard(card);
        return CardMapper.toDto(card);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")
    public CardDto replaceCard(@Valid @RequestBody CardDto newCardDto, @PathVariable int id) throws CardNotFoundException {
        Card card = CardMapper.toEntity(newCardDto);
        card = cardService.replaceCard(card, id);
        return CardMapper.toDto(card);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")
    public CardDto partialUpdateCard(@Valid @RequestBody CardDto updates, @PathVariable int id) throws CardNotFoundException {
        Card card = CardMapper.toEntity(updates);
        card = cardService.partialUpdateCard(card, id);
        return CardMapper.toDto(card);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")
    public void deleteCard(@PathVariable int id) throws CardNotFoundException {
        cardService.deleteCard(id);
    }

    @GetMapping("/{id}/assoc-options")
    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")
    public GetAvailResponce getCardAvailAssoc(@PathVariable("id") int cardId, @RequestParam(value = "assoc", required = false) AssocType assocType) throws CardNotFoundException {

        List<Card> cards = cardService.getCardAvailAssoc(cardId, assocType);

        List<CardSummaryDto> dtos = cards.stream()
                .map(card -> new CardSummaryDto(card.getCardId(), card.getName()))
                .toList();

        return new GetAvailResponce(dtos);
    }


}
