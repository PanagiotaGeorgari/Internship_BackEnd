package com.logicea.cards.repository;

import com.logicea.cards.entity.Assoc;
import com.logicea.cards.enums.AssocType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface AssocRepository extends JpaRepository<Assoc, Integer> {
    Optional<Assoc> findByLcardIdAndRcardIdAndAssoc(Integer lcardId, Integer rcardId, AssocType assocType);

    Optional<Assoc> findByRcardIdAndLcardId(Integer rcardId, Integer lcardId);

    Collection<? extends Assoc> findByLcardId(Integer lcardId);

    Collection<? extends Assoc> findByRcardIdAndType(Integer rcardId, AssocType assocType);

    Collection<? extends Assoc> findByLcardIdAndType(int cardId, AssocType assocType);

    //List<Assoc> findAssocByAssocType(AssocType assocType);

}
