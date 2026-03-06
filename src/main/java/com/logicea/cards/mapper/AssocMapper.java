package com.logicea.cards.mapper;

import com.logicea.cards.dto.AssocDto;
import com.logicea.cards.dto.AssocInputDto;
import com.logicea.cards.entity.Assoc;

public class AssocMapper {

    public static AssocDto toDto(Assoc assoc) {
        AssocDto assocDto = new AssocDto(assoc.getId(),assoc.getLcardId(),assoc.getRcardId(),assoc.getAssoc(),null);
        return assocDto;
    }

    public static Assoc toEntity(AssocDto assocDto) {
        Assoc assoc = new Assoc();
        assoc.setLcardId(assocDto.lcardId());
        assoc.setRcardId(assocDto.rcardId());
        assoc.setAssoc(assocDto.assoc());
        return assoc;
    }
    public static Assoc toEntity(AssocInputDto input) {
        Assoc assoc = new Assoc();
        assoc.setLcardId(input.lcardId());
        assoc.setRcardId(input.rcardId());
        assoc.setAssoc(input.assoc());
        return assoc;
    }

}
