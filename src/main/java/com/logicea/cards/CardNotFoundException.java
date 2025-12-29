package com.logicea.cards;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//@ResponseStatus(code=HttpStatus.NOT_FOUND,reason ="CardId Not Found")
public//set custom HTTP status codes for controller methods
class CardNotFoundException extends RuntimeException {
     public CardNotFoundException(int id) {
        super("Card with id " + id + " not found");
     }
}

