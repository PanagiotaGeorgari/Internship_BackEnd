package com.logicea.cards;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ControllerAdvice
public class AssocControllerExceptions {

    @ExceptionHandler(AssocAlreadyExistsException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Assoc Already exists!")
    public ResponseEntity<Map<String, String>> handleAssocExists(AssocAlreadyExistsException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }

}
