package com.logicea.cards;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController // i prefer to return data (JSON) rather than HTML
@RequestMapping("/api/cards") // in which path is my base and i can do my  requests

public class CardController {
    @Autowired
    private final CardRepository repository; //is the interface between my base and my backend
                                            //via this i have access to my base

    CardController(CardRepository repository) {
        this.repository = repository;
    }

    @GetMapping             //HTTP GET requests in RESTful web services
                            // It simplifies mapping URLs to specific controller methods
    public Iterable<Card> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{cardId}")    //@PathVariable we gte the cardId from the url path of the request
    Card getById(@PathVariable int cardId) throws CardNotFoundException {
        return repository.findById(cardId).orElseThrow(() -> new CardNotFoundException(cardId));
    }

    @PostMapping            //HTTP POST requests in RESTful web services
                                // @Valid is for validation , begins the validation for card's elements
    Card newCard(@Valid @RequestBody Card newCard) { //bind the body of an HTTP request to a method parameter in a controller handler method
        return repository.save(newCard);
    }

    @PutMapping("/{cardId}")//HTTP PUT requests in RESTful web services
    Card replaceCard(@Valid @RequestBody Card newCard, @PathVariable int cardId) throws CardNotFoundException {
        return repository.findById(cardId).map(card -> {
            card.setCardId(newCard.getCardId());
            card.setName(newCard.getName());
            card.setDescription(newCard.getDescription());
            card.setColor(newCard.getColor());
            card.setStatus(newCard.getStatus());
            card.setUserId(newCard.getUserId());
            return repository.save(newCard);
        }).orElseThrow(() -> new CardNotFoundException(cardId));
    }


    @PatchMapping("/{cardId}")//HTTP PATCH requests in RESTful web services
    Card partialUpdateCard(@Valid @RequestBody Card updates, @PathVariable int cardId) throws CardNotFoundException {
        return repository.findById(cardId).map(card -> {
            if (updates.getName() != null) {
                card.setName(updates.getName());
            }
            if (updates.getDescription() != null) {
                card.setDescription(updates.getDescription());
            }
            if (updates.getColor() != null) {
                card.setColor(updates.getColor());
            }
            if (updates.getStatus() != null) {
                card.setStatus(updates.getStatus());
            }
            if (updates.getUserId() != 0) {
                card.setUserId(updates.getUserId());
            }

            return repository.save(card);
        }).orElseThrow(() -> new CardNotFoundException(cardId));
    }

    @DeleteMapping("/{cardId}")//HTTP DELETE requests in RESTful web services
    void deleteCard(@PathVariable int cardId) throws CardNotFoundException {
        if(repository.findById(cardId).isPresent()) {
            repository.deleteById(cardId);
        }
        else {
            throw new CardNotFoundException(cardId);
        }
    }
}
