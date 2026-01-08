package com.logicea.cards.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.logicea.cards.enums.AssocType;

public record AssocDto(
        Integer id,
        @JsonProperty("lcardId") int lcardId,
        @JsonProperty("assoc") AssocType assoc,
        @JsonProperty("rcardId") int rcardId
) {}