package com.logicea.cards.dto;

import com.logicea.cards.enums.CardStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;


public record CardDto(
        int cardId,
        @NotBlank(message = "name field is mandatory")
        @Size(min = 1, max = 50, message = "is between 1 - 50 characters")
        String name,
        @Size(max = 255, message = "description can not be over 255 chars")
        String description,
        @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$|^$", message = "Invalid format of color e.g #ABC123")
        String color,
        CardStatus status,
        int createdBy,
        Instant createdAt,
        Instant updatedAt,
        int updatedBy,
        CardSummaryDto card) {
}
