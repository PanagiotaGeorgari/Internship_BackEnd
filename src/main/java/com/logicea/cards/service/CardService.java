package com.logicea.cards.service;

import com.logicea.cards.CardNotFoundException;
import com.logicea.cards.GetByIdResponse;
import com.logicea.cards.PaginationResponse;
import com.logicea.cards.dto.CardDto;
import com.logicea.cards.enums.AssocType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.file.AccessDeniedException;


public interface CardService {

    //List<CardDto> getAll();
    GetByIdResponse getById(int cardId) throws CardNotFoundException, AccessDeniedException;

    CardDto newCard(CardDto newCard);

    CardDto replaceCard(CardDto newCard, int id) throws CardNotFoundException;

    CardDto partialUpdateCard(CardDto updates, int id) throws CardNotFoundException;

    void deleteCard(int id) throws CardNotFoundException;

    PaginationResponse<CardDto> getCardsPagination(int page, int size, String sort);

    void getCardAvailAssoc(@PathVariable("id") int cardId, @RequestParam(value = "assoc", required = false) AssocType assocType) throws CardNotFoundException;


}
