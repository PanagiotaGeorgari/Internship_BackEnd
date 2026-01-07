package com.logicea.cards.service;

import com.logicea.cards.CardNotFoundException;
import com.logicea.cards.dto.AssocDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface AssocService {
    List<Integer> newAssoc(@RequestBody AssocDto newAssoc);

    void deleteAssoc(@PathVariable int id);/*throws assocNotFoundException*/
}