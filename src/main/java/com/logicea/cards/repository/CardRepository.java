package com.logicea.cards.repository;
import com.logicea.cards.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

//repository is the way to interact with my base's data
//i have access to basic methods such us findAll(),save(),delete()
//Card is the class which is also the entity
//Integer is the type of the primary key

public interface  CardRepository extends JpaRepository<Card,Integer> {

}