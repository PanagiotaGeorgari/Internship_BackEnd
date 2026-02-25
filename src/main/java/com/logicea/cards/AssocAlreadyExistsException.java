package com.logicea.cards;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AssocAlreadyExistsException extends RuntimeException {
    public AssocAlreadyExistsException(String message) {
        super(message);
    }
}

