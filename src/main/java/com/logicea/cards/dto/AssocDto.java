package com.logicea.cards.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.logicea.cards.enums.Assoc;

public record AssocDto(
        Integer id,
        @JsonProperty("lcardId") int lcardId,
        @JsonProperty("assoc") Assoc assoc,
        @JsonProperty("rcardId") int rcardId
) {}