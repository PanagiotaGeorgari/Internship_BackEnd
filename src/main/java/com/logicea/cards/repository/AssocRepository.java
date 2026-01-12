package com.logicea.cards.repository;

import com.logicea.cards.entity.Assoc;
import com.logicea.cards.enums.AssocType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssocRepository extends JpaRepository<Assoc,Integer> {
    Optional<Assoc> findByLcardIdAndRcardIdAndAssoc(Integer lcardId, Integer rcardId, AssocType assocType);
    Optional<Assoc> findByRcardIdAndLcardId(Integer rcardId, Integer lcardId);
    List<Assoc> findByRcardId(Integer rcardId);
    List<Assoc> findByLcardId(Integer lcardId);
    List<Assoc> findAssocByAssocType(AssocType assocType);

}
