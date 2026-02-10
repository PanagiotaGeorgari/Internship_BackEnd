package com.logicea.cards.entity;

import com.logicea.cards.dto.CardSummaryDto;
import com.logicea.cards.enums.AssocType;
import jakarta.persistence.*;

@Entity
@Table(name = "card_assocs")
public class Assoc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "lcard_id")
    private int lcardId;

    @Enumerated(EnumType.STRING)
    @Column(name = "assoc", nullable = false)
    private AssocType assoc;

    @Column(name = "rcard_id")
    private int rcardId;

    @Transient
    private CardSummaryDto cardsummaryDto;

    public Assoc() {

    }

    public Assoc(int id, int lcardId, AssocType assoc, int rcardId, CardSummaryDto cardSummaryDto) {
        this.id = id;
        this.lcardId = lcardId;
        this.assoc = assoc;
        this.rcardId = rcardId;
        this.cardsummaryDto = cardSummaryDto;
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

    public AssocType getAssoc() {
        return assoc;
    }

    public void setAssoc(AssocType assoc) {
        this.assoc = assoc;
    }

    public int getRcardId() {
        return rcardId;
    }

    public void setRcardId(int rcardId) {
        this.rcardId = rcardId;
    }
}
