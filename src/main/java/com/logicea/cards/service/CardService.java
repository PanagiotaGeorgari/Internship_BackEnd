package com.logicea.cards.service;

import com.logicea.cards.CardNotFoundException;
import com.logicea.cards.GetByIdResponse;
import com.logicea.cards.entity.Card;
import com.logicea.cards.enums.AssocType;
import org.springframework.data.domain.Page;

import java.nio.file.AccessDeniedException;
import java.util.List;


public interface CardService {


    GetByIdResponse getById(int cardId) throws CardNotFoundException, AccessDeniedException;

    Card newCard(Card newCard);

    Card replaceCard(Card newCard, int id) throws CardNotFoundException;

    Card partialUpdateCard(Card updates, int id) throws CardNotFoundException;

    void deleteCard(int id) throws CardNotFoundException;

    Page<Card> getCardsPagination(int page, int size, String sort);

    List<Card> getCardAvailAssoc(int cardId, AssocType assocType) throws CardNotFoundException;


}
