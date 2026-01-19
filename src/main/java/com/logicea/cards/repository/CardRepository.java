package com.logicea.cards.repository;

import com.logicea.cards.dto.AssocDto;
import com.logicea.cards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

//repository is the way to interact with my base's data
//i have access to basic methods such us findAll(),save(),delete()
//Card is the class which is also the entity
//Integer is the type of the primary key

public interface CardRepository extends JpaRepository<Card, Integer> {
    Page<Card> findByCreatedBy(int userId, Pageable pageable);

    List<Card> findByCreatedBy(int userId);

    @Query("SELECT new com.logicea.cards.dto.AssocDto(assoc.id, assoc.assoc, " +
            "new com.logicea.cards.dto.CardSummaryDto(c.cardId, c.name)) " +
            "FROM Assoc assoc " +
            "JOIN Card c ON assoc.rcardId = c.cardId " +
            "WHERE assoc.lcardId = :id")
    List<AssocDto> findAssociationsAsDto(@Param("id") int id);
}
