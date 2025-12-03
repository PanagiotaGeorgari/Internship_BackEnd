package com.logicea.cards;
import jakarta.persistence.*;

@Entity //this classs is also an entity in my sql base
@Table(name="cards") //this class interacts with the table cards in database

public class Card {
    @Id // the primary key is the card_id
    @GeneratedValue(strategy = GenerationType.AUTO) // automated generated the card_id (int)

    private int card_id;
    private String name;
    private String description;
    private String color;

    @Enumerated(EnumType.STRING) //stores the enum status as string in base
    private card_status status;

    private int user_id;

    //constructor
    public Card(int card_id, String name, String description, String color, card_status status, int user_id) {
        this.card_id = card_id;
        this.name = name;
        this.description = description;
        this.color = color;
        this.status = status;
        this.user_id = user_id;
    }

    //empty constructor
    public Card() {
    }

    //------getters / setters------------------------------
    public int getCard_id() {
        return card_id;
    }

    public void setCard_id(int card_id) {
        this.card_id = card_id;
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

    public card_status getStatus() {
        return status;
    }

    public void setStatus(card_status status) {
        this.status = status;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    //-------------------------------------------------

    public String toString() {
        System.out.println("Card ID: " + card_id+"\nName: "+name+"\nDescription: "+description+"\nColor: "+color+"\nStatus: "+status+"\nUserID: "+user_id);
        return null;
    }
}

