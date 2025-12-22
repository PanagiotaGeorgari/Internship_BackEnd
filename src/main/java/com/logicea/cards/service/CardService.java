package com.logicea.cards.service;

import com.logicea.cards.CardNotFoundException;
import com.logicea.cards.PaginationResponse;
import com.logicea.cards.dto.CardDto;
import com.logicea.cards.entity.Card;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;


public interface CardService  {

    List<CardDto> getAll();
    Optional<Card> getById(@PathVariable int cardId) throws CardNotFoundException, AccessDeniedException;
    CardDto newCard(@Valid @RequestBody CardDto newCard);
    CardDto replaceCard(@Valid @RequestBody CardDto newCard, @PathVariable int id) throws CardNotFoundException;
    CardDto partialUpdateCard(@Valid @RequestBody CardDto updates, @PathVariable int id) throws CardNotFoundException;
    void deleteCard(@PathVariable int id) throws CardNotFoundException;
    PaginationResponse<CardDto> getCardsPagination(@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "5") int size, @RequestParam(name = "sort", defaultValue = "cardId") String sort);


}
