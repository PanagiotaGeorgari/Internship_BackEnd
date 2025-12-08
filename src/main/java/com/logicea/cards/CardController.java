package com.logicea.cards;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards") // in which path is my base and i can do my  requests
public class CardController {
    @Autowired
    private final CardRepository repository;

    CardController(CardRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Iterable<Card> all() { //postman checked
        return repository.findAll();
    }

    @GetMapping("/{card_id}")
    Card one(@PathVariable int card_id) throws CardNotFoundException {
        return repository.findById(card_id).orElseThrow(() -> new CardNotFoundException(card_id));
    }

    @PostMapping
    Card newCard(@Valid @RequestBody Card newCard) {
        return repository.save(newCard);
    }

    @PutMapping("/{id}")
    Card replaceCard(@Valid @RequestBody Card newCard, @PathVariable int id) throws CardNotFoundException {
        return repository.findById(id).map(card -> {
            card.setCardId(newCard.getCardId());
            card.setName(newCard.getName());
            card.setDescription(newCard.getDescription());
            card.setColor(newCard.getColor());
            card.setStatus(newCard.getStatus());
            card.setUserId(newCard.getUserId());
            return repository.save(newCard);
        }).orElseThrow(() -> new CardNotFoundException(id));
    }


    @PatchMapping("/{id}")
    Card partialUpdateCard(@Valid @RequestBody Card updates, @PathVariable int id) throws CardNotFoundException {
        return repository.findById(id).map(card -> {
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
        }).orElseThrow(() -> new CardNotFoundException(id));
    }

    @DeleteMapping("/{id}")
    void deleteCard(@PathVariable int id) throws CardNotFoundException {
        if(repository.findById(id).isPresent()) {
            repository.deleteById(id);
        }
        else {
            throw new CardNotFoundException(id);
        }
    }


}
