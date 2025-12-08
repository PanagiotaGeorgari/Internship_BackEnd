package com.logicea.cards;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.NOT_FOUND,reason ="CardId Not Found")
class CardNotFoundException extends RuntimeException {
     CardNotFoundException(int id) {
        super("Card with id " + id + " not found");
     }
}

