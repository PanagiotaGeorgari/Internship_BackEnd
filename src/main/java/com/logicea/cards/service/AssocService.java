package com.logicea.cards.service;

import com.logicea.cards.dto.AssocDto;
import com.logicea.cards.entity.Assoc;
import com.logicea.cards.enums.AssocType;

import java.util.Collection;
import java.util.List;

public interface AssocService {
    List<Integer> newAssoc(Assoc newAssoc);

    void deleteAssoc(int id);

    List<AssocDto> getCardAssocs(int cardId);

    Collection<Assoc> getCardAssocsByType(int cardId, AssocType assocType);
}