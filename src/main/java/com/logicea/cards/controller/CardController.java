package com.logicea.cards.controller;

import com.logicea.cards.CardNotFoundException;
import com.logicea.cards.PaginationResponse;
import com.logicea.cards.dto.CardDto;
import com.logicea.cards.service.CardService;
import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    // Dependency Injection μέσω Constructor
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")
    public List<CardDto> getAll() {
        System.out.println("inside getAll CardController");
        return cardService.getAll();
    }

    @GetMapping("/{cardId}")
    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")
    public CardDto getById(@PathVariable int cardId) throws CardNotFoundException {
        return cardService.getById(cardId);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")
    public CardDto newCard(@Valid @RequestBody CardDto newCardDto) {
        return cardService.newCard(newCardDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")
    public CardDto replaceCard(@Valid @RequestBody CardDto newCardDto, @PathVariable int id) throws CardNotFoundException {
        return cardService.replaceCard(newCardDto, id);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")
    public CardDto partialUpdateCard(@Valid @RequestBody CardDto updates, @PathVariable int id) throws CardNotFoundException {
        return cardService.partialUpdateCard(updates, id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")
    public void deleteCard(@PathVariable int id) throws CardNotFoundException {
        cardService.deleteCard(id);
    }

    @GetMapping("/page")
    public PaginationResponse<CardDto> getCardsPagination(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sort", defaultValue = "cardId") String sort) {
        return cardService.getCardsPagination(page, size, sort);
    }
}
