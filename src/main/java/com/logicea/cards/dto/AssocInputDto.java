package com.logicea.cards.dto;

import com.logicea.cards.enums.AssocType;
import jakarta.validation.constraints.NotNull;

public record AssocInputDto(
        @NotNull Integer lcardId,
        @NotNull Integer rcardId,
        @NotNull AssocType assoc
) {}