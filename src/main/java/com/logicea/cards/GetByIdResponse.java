package com.logicea.cards;

import com.logicea.cards.dto.AssocDto;
import com.logicea.cards.entity.Assoc;
import com.logicea.cards.entity.Card;

import java.util.List;


public class GetByIdResponse {
    Card card;
    List<AssocDto> assocs;

    public GetByIdResponse(Card card, List<AssocDto> assocs) {
        this.card = card;
        this.assocs = assocs;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public List<AssocDto> getAssocs() {
        return assocs;
    }

    public void setAssocs(List<AssocDto> assocs) {
        this.assocs = assocs;
    }
}
