package com.logicea.cards;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Assoc Not Found")
public class AssocNotFoundException extends RuntimeException {
    public AssocNotFoundException(String id) {
        super("Assoc with id " + id + " not found");
    }

}