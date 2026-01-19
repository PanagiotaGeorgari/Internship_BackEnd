package com.logicea.cards.repository;

import com.logicea.cards.entity.Assoc;
import com.logicea.cards.enums.AssocType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface AssocRepository extends JpaRepository<Assoc, Integer> {
    Optional<Assoc> findByLcardIdAndRcardIdAndAssoc(Integer lcardId, Integer rcardId, AssocType assocType);

    Optional<Assoc> findByRcardIdAndLcardId(Integer rcardId, Integer lcardId);

    Collection<Assoc> findByRcardId(int rcardId);

    Collection<Assoc> findByLcardId(int lcardId);

    Collection<Assoc> findByLcardIdAndAssoc(Integer lcardId, AssocType assocType);


}
