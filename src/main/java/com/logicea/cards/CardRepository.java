package com.logicea.cards;
import com.logicea.cards.Card;
import org.springframework.data.repository.CrudRepository;//i have to requests

//repository is the way to interact with my base's data
//i have access to basic methods such us findAll(),save(),delete()
//Card is the class which is also the entity
//Integer is the type of the primary key

public interface  CardRepository extends CrudRepository<Card,Integer> {
}