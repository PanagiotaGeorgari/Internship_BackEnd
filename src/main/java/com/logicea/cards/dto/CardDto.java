package com.logicea.cards.dto;

import com.logicea.cards.enums.CardStatus;

import java.time.Instant;


public record CardDto(int cardId, String name, String description, String color, CardStatus status, int createdBy,
                      Instant createdAt, Instant updatedAt, int updatedBy) {
}
