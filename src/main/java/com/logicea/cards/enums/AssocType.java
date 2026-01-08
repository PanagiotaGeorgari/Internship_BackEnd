package com.logicea.cards.enums;

public enum AssocType {
    BLOCKS,
    CHILD_OF,
    BLOCKED_BY,
    PARENT_OF;
    public AssocType getInverseAssoc(){
        return switch (this){
            case BLOCKS -> BLOCKED_BY;
            case CHILD_OF -> PARENT_OF;
            case BLOCKED_BY -> BLOCKS;
            case PARENT_OF -> CHILD_OF;
        };
    }
}