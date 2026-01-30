package com.logicea.cards.controller;

import com.logicea.cards.dto.AssocDto;
import com.logicea.cards.entity.Assoc;
import com.logicea.cards.service.AssocService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping()
public class AssocController {
    private final AssocService assocService;

    public AssocController(AssocService assoc) {
        this.assocService = assoc;
    }

    @PostMapping("/card-assocs")
    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")
    public ResponseEntity<Map<String, List<Integer>>> createAssoc(@RequestBody Assoc assoc) throws AccessDeniedException {
        List<Integer> ids = assocService.newAssoc(assoc);
        return ResponseEntity.ok(Map.of("ids", ids));
    }

    @DeleteMapping("/card-assocs/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")
    public void deleteAssoc(@PathVariable int id) throws AccessDeniedException {
        assocService.deleteAssoc(id);
    }

}
