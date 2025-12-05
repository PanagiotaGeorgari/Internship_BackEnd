package com.logicea.cards;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity //this classs is also an entity in my sql base
@Table(name="cards") //this class interacts with the table cards in database
public class Card {
    @Id // the primary key is the card_id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // automated generated the card_id (int)

    @Column(name = "card_id")
    private int cardId;
    private String name;
    private String description;
    private String color;

    @Enumerated(EnumType.STRING) //stores the enum status as string in base
    private CardStatus status=setStatus(CardStatus.valueOf("TODO"));

    @Column(name = "user_id")
    @JsonProperty("user_id")
    private int userId;

    //------getters / setters------------------------------
    public int getCardId() {
        return cardId;
    }

    public void setCardId(int card_id) {
        this.cardId = card_id;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    //-------------------------------------------------

    public String toString() {
        System.out.println("Card ID: " + cardId +"\nName: "+name+"\nDescription: "+description+"\nColor: "+color+"\nStatus: "+status+"\nUserID: "+ userId);
        return null;
    }
}

