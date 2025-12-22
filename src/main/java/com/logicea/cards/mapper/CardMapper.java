package com.logicea.cards.mapper;
import com.logicea.cards.dto.CardDto;
import com.logicea.cards.entity.Card;

public class CardMapper {

    public static CardDto toDto(Card card) {
        CardDto cardDto= new CardDto(card.getCardId(),card.getName(), card.getDescription(), card.getColor(), card.getStatus(),
                                    card.getCreatedBy(),card.getCreatedAt(),card.getUpdatedAt(),card.getUpdatedBy())  ;
        return cardDto;
    }

    public static Card toEntity(CardDto cardDto) {
        Card card = new Card();
        card.setName(cardDto.name());
        card.setDescription(cardDto.description());
        card.setColor(cardDto.color());
        if (cardDto.status() != null) {
            card.setStatus(cardDto.status());
        }

        card.setCreatedBy(cardDto.createdBy());
        return card;
    }

    /*public static void updateEntityFromDto(CardDto dto, Card entity) {
        if (dto == null) return;
        if (dto.name() != null) entity.setName(dto.name());
        if (dto.description() != null) entity.setDescription(dto.description());
        if (dto.color() != null) entity.setColor(dto.color());
        if (dto.status() != null) entity.setStatus(dto.status());
        if (dto.createdBy() != 0) entity.setCreatedBy(dto.createdBy());

    }*/
}
