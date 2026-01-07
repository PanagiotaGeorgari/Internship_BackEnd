package com.logicea.cards;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class AssocControllerEcxeptions {

    @ExceptionHandler(AssocAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleAssocExists(AssocAlreadyExistsException ex) {
        // Επιστρέφει JSON: {"message": "This association already exists!"}
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }
}
