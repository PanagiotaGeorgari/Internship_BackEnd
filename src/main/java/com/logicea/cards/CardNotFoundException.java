package com.logicea.cards;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.NOT_FOUND,reason ="CardId Not Found")//set custom HTTP status codes for controller methods
class CardNotFoundException extends RuntimeException {
     CardNotFoundException(int id) {
        super("Card with id " + id + " not found");
     }
}

