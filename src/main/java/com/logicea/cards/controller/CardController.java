package com.logicea.cards.controller;

import com.logicea.cards.CardNotFoundException;
import com.logicea.cards.GetAvailResponce;
import com.logicea.cards.GetByIdResponse;
import com.logicea.cards.PaginationResponse;
import com.logicea.cards.dto.CardDto;
import com.logicea.cards.enums.AssocType;
import com.logicea.cards.service.CardService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;


    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")
    public PaginationResponse<CardDto> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "cardId") String sort) {

        return cardService.getCardsPagination(page, size, sort);
    }

    @GetMapping("/{cardId}")
    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")
    public GetByIdResponse getById(@PathVariable int cardId) throws CardNotFoundException, AccessDeniedException {
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

    @GetMapping("/{id}/assoc-options")
    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")
    public GetAvailResponce getCardAvailAssoc(@PathVariable("id") int cardId, @RequestParam(value = "assoc", required = false) AssocType assocType) throws CardNotFoundException {
        return cardService.getCardAvailAssoc(cardId, assocType);
    }


}
