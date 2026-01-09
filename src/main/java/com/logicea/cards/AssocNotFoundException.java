package com.logicea.cards;


public//set custom HTTP status codes for controller methods
class AssocNotFoundException extends RuntimeException {
    public AssocNotFoundException(String id) {
        super("Asooc with id " + id + " not found");
    }

}