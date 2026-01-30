package com.logicea.cards;

import com.logicea.cards.dto.CardSummaryDto;

import java.util.List;

public class GetAvailResponce {
    List<CardSummaryDto> data;

    public GetAvailResponce(List<CardSummaryDto> cards) {
        this.data = cards;
    }

    public List<CardSummaryDto> getCards() {
        return data;
    }

    public void setCards(List<CardSummaryDto> cards) {
        this.data = cards;
    }
}
