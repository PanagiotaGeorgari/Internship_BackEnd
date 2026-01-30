package com.logicea.cards.repository;

import com.logicea.cards.dto.AssocDto;
import com.logicea.cards.entity.Assoc;
import com.logicea.cards.enums.AssocType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AssocRepository extends JpaRepository<Assoc, Integer> {
    Optional<Assoc> findByLcardIdAndRcardIdAndAssoc(Integer lcardId, Integer rcardId, AssocType assocType);

    Optional<Assoc> findByRcardIdAndLcardId(Integer rcardId, Integer lcardId);

    Collection<Assoc> findByRcardId(int rcardId);

    Collection<Assoc> findByLcardId(int lcardId);

    Collection<Assoc> findByLcardIdAndAssoc(Integer lcardId, AssocType assocType);

    @Query("SELECT new com.logicea.cards.entity.Assoc(assoc.id,assoc.lcardId, assoc.assoc, assoc.rcardId , " +
            "new com.logicea.cards.dto.CardSummaryDto(c.cardId, c.name)) " +
            "FROM Assoc assoc " +
            "JOIN Card c ON assoc.rcardId = c.cardId " +
            "WHERE assoc.lcardId = :id")
    List<Assoc> findAssociationsAsDto(@Param("id") int id);
}
