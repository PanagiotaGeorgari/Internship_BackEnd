package com.logicea.cards;


public//set custom HTTP status codes for controller methods
class AssocNotFoundException extends RuntimeException {
    public AssocNotFoundException(int id) {
        super("Asooc with id " + id + " not found");
    }

    public static class AssocAlreadyExistsException extends RuntimeException {
        public AssocAlreadyExistsException(String message) {
            super(message);
        }
    }
}