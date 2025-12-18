package com.logicea.cards.entity;
import com.logicea.cards.enums.CardStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;


@Entity //this classs is also an entity in my sql base
@Table(name="cards") //this class interacts with the table cards in database
public class Card {
    @Id // the primary key is the card_id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // automated generated the card_id (int)
    @Column(name = "card_id")
    private int cardId;

    @NotBlank(message = "name field is mandatory")
    @Size(min = 2, max = 50, message = "is between 2 - 50 characters")
    private String name;

    @Size(max = 255, message = "description can not be over 255 chars")
    private String description;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$|^$", message = "Invalid format of color e.g #ABC123")
    private String color;

    @NotNull(message = "status can not be empty")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CardStatus status=CardStatus.TODO;

    @Column(name = "created_by")
    private int createdBy;

    @Column(name="created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Instant createdAt;

    @Column (name="updated_by")
    private String updatedBy;

    @Column(name="updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Instant updatedAt;

    public Card(int i, String name, String description, String color, CardStatus status, int i1, Instant instant, String s, Instant instant1) {
        this.cardId = i;
        this.name = name;
        this.description = description;
        this.color = color;
        this.status = status;
        this.createdBy = i1;
        this.createdAt = instant;
        this.updatedBy=s;
        this.updatedAt = instant1;
    }

    public Card() {}

    //------getters / setters------------------------------
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

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    //-------------------------------------------------

    public String toString() {
        System.out.println("Card ID: " + cardId +"\nName: "+name+"\nDescription: "+description+"\nColor: "+color+"\nStatus: "+status+"\nCreatedBy: "+ createdBy+"\nUpdatedBy: "+updatedBy+"\nUpdatedAt: "+updatedAt);
        return null;
    }
}

