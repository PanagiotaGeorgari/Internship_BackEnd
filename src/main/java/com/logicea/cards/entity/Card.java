package com.logicea.cards.entity;

import com.logicea.cards.enums.CardStatus;
import jakarta.persistence.*;

import java.time.Instant;


@Entity //this classs is also an entity in my sql base
@Table(name = "cards") //this class interacts with the table cards in database
public class Card {
    @Id // the primary key is the card_id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // automated generated the card_id (int)
    @Column(name = "card_id")
    private int cardId;

    private String name;


    private String description;


    private String color;


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CardStatus status = CardStatus.TODO;

    @Column(name = "created_by")
    private int createdBy;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Instant createdAt;

    @Column(name = "updated_by")
    private int updatedBy;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Instant updatedAt;

    public Card(int i, String name, String description, String color, CardStatus status, int i1, Instant instant, int s, Instant instant1) {
        this.cardId = i;
        this.name = name;
        this.description = description;
        this.color = color;
        this.status = status;
        this.createdBy = i1;
        this.createdAt = instant;
        this.updatedBy = s;
        this.updatedAt = instant1;
    }

    public Card() {
    }


    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public CardStatus getStatus() {
        return status;
    }

    public CardStatus setStatus(CardStatus status) {
        this.status = status;
        return status;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public int getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(int updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }


    public String toString() {
        System.out.println("Card ID: " + cardId + "\nName: " + name + "\nDescription: " + description + "\nColor: " + color + "\nStatus: " + status + "\nCreatedBy: " + createdBy + "\nUpdatedBy: " + updatedBy + "\nUpdatedAt: " + updatedAt);
        return null;
    }
}

