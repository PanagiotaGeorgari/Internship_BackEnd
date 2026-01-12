package com.logicea.cards;

import com.logicea.cards.entity.Assoc;
import com.logicea.cards.entity.Card;
import java.util.List;


public class GetByIdResponse {
    Card card;
    List<Assoc> assocs;

    public GetByIdResponse(Card card, List<Assoc> assocs) {
        this.card = card;
        this.assocs = assocs;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public List<Assoc> getAssocs() {
        return assocs;
    }

    public void setAssocs(List<Assoc> assocs) {
        this.assocs = assocs;
    }
}
