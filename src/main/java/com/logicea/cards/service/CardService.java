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

    //List<CardDto> getAll();
    Optional<Card> getById(int cardId) throws CardNotFoundException, AccessDeniedException;
    CardDto newCard(CardDto newCard);
    CardDto replaceCard( CardDto newCard, int id) throws CardNotFoundException;
    CardDto partialUpdateCard(CardDto updates,int id) throws CardNotFoundException;
    void deleteCard(int id) throws CardNotFoundException;
    PaginationResponse<CardDto> getCardsPagination(int page,  int size,  String sort);


}
