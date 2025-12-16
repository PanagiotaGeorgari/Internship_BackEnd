package com.logicea.cards;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    /*@GetMapping("/{offset}/{pagesize}/{field}")
    public Iterable<Card> getCardsPagenation(@PathVariable int offset, @PathVariable int pagesize, @PathVariable String field) {
        Pageable pageable = PageRequest.of(offset, pagesize).withSort(Sort.by(Sort.Direction.ASC, field));
        return repository.findAll(pageable);
    }*/
    @GetMapping("/")
    public PaginationResponse<Card> getCardsPagination(@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "5") int size,@RequestParam(name = "sort", defaultValue = "cardId") String sort)
        {
        // @RequestParam takes the value from the link
        // @RequestParam if the value of the variable is missing then defaults with a sensible value
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sort));
        Page<Card> result = repository.findAll(pageable);
        return new PaginationResponse<>(page,size,sort,result.getTotalPages(),result.getContent());
    }

}
