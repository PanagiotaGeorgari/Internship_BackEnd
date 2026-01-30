package com.logicea.cards.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.logicea.cards.enums.AssocType;

public record AssocDto(
        Integer id,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // i use it in my code but i do not want to print it in postman
        Integer lcardId,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        Integer rcardId,
        AssocType assoc,
        CardSummaryDto card // infos for the other card
) {
}

