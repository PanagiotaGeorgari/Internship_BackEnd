package com.logicea.cards.repository;

import com.logicea.cards.entity.Assoc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssocRepository extends JpaRepository<Assoc,Integer> {

    Optional<Assoc> findByRcardId(Integer integer);

    Optional <Assoc> findByLcardId(Integer integer);
}
