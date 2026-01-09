package com.logicea.cards.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.logicea.cards.enums.AssocType;

public record AssocDto(
        Integer id,
        int lcardId,
        AssocType assoc,
        int rcardId
) {

}