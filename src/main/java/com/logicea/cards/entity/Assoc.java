package com.logicea.cards.entity;

import jakarta.persistence.*;

@Entity
@Table(name ="card_assocs" )
public class Assoc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id")
    private int id;

    @Column(name = "lcard_id")
    private int lcardId;

    @Enumerated(EnumType.STRING)
    @Column(name = "assoc", nullable = false)
    private com.logicea.cards.enums.Assoc assoc;

    @Column(name="rcard_id")
    private int rcardId;

    public Assoc(int id, int lcardId, com.logicea.cards.enums.Assoc assoc, int rcardId) {
        this.id = id;
        this.lcardId = lcardId;
        this.assoc = assoc;
        this.rcardId = rcardId;
    }

    public Assoc() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLcardId() {
        return lcardId;
    }

    public void setLcardId(int lcardId) {
        this.lcardId = lcardId;
    }

    public com.logicea.cards.enums.Assoc getAssoc() {
        return assoc;
    }

    public void setAssoc(com.logicea.cards.enums.Assoc assoc) {
        this.assoc = assoc;
    }

    public int getRcardId() {
        return rcardId;
    }

    public void setRcardId(int rcardId) {
        this.rcardId = rcardId;
    }
}
