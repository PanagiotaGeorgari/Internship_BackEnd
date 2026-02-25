package com.logicea.cards;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AssocControllerExceptions {

    @ExceptionHandler(AssocAlreadyExistsException.class)
    public ResponseEntity<String> handleAssocExists() {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Assoc Already exists!");
    }
}