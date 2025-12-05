package com.logicea.cards;
class CardNotFoundException extends RuntimeException {
     CardNotFoundException(int id) {
        super("Card with id " + id + " not found");
     }
}

