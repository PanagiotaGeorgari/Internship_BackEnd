package com.logicea.cards.service.impl;

import com.logicea.cards.AssocAlreadyExistsException;
import com.logicea.cards.AssocNotFoundException;
import com.logicea.cards.CardNotFoundException;

import com.logicea.cards.dto.AssocDto;
import com.logicea.cards.entity.Assoc;
import com.logicea.cards.entity.Card;
import com.logicea.cards.entity.User;
import com.logicea.cards.enums.AssocType;
import com.logicea.cards.enums.UserRole;
import com.logicea.cards.repository.AssocRepository;
import com.logicea.cards.repository.CardRepository;
import com.logicea.cards.service.AssocService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class AssocServiceImpl implements AssocService {
    private final AssocRepository assocRepository;
    private final CardRepository cardRepository;

    public AssocServiceImpl(AssocRepository assocRepository, CardRepository cardRepository) {
        this.assocRepository = assocRepository;
        this.cardRepository = cardRepository;
    }


    public List<Integer> newAssoc(Assoc assoc) {
        Card lcard = cardRepository.findById(assoc.getLcardId())
                .orElseThrow(() -> new CardNotFoundException(assoc.getLcardId()));
        Card rcard = cardRepository.findById(assoc.getRcardId())
                .orElseThrow(() -> new CardNotFoundException(assoc.getRcardId()));
        User user = getCurrentUser();//take user

        if (!validateOwner(lcard, rcard, user)) {
            throw new AccessDeniedException("Members can only associate their own cards!");
        }
        if (!uniqueAssoc(lcard, rcard, assoc.getAssoc())) {
            throw new AssocAlreadyExistsException("These cards are already associated!");
        }

        Assoc a1 = createAssoc(lcard.getCardId(), rcard.getCardId(), assoc.getAssoc());
        Assoc a2 = createAssoc(rcard.getCardId(), lcard.getCardId(), assoc.getAssoc().getInverseAssoc());

        Assoc s1 = assocRepository.save(a1);
        Assoc s2 = assocRepository.save(a2);
        return List.of(s1.getId(), s2.getId());

    }


    public boolean validateOwner(Card lcard, Card rcard, User user) {
        if (user.getRole() == UserRole.ADMIN) {
            return true;
        } else {
            boolean ownerLeftCard = lcard.getCreatedBy() == user.getUserId(); //check if cards belong to this member
            boolean ownerRightCard = rcard.getCreatedBy() == user.getUserId();
            System.out.println("member newAssoc");
            // if cards do not belong to the same user
            return ownerLeftCard && ownerRightCard;
        }
    }

    public boolean uniqueAssoc(Card lcard, Card rcard, AssocType type) {

        Optional<Assoc> existingAssoc = assocRepository.findByLcardIdAndRcardIdAndAssoc(
                lcard.getCardId(),
                rcard.getCardId(),
                type
        );
        Optional<Assoc> inversedexistingAssoc = assocRepository.findByLcardIdAndRcardIdAndAssoc(
                rcard.getCardId(),
                lcard.getCardId(),
                type
        );
        return existingAssoc.isEmpty() && inversedexistingAssoc.isEmpty();

    }

    private Assoc createAssoc(int l, int r, AssocType type) {
        Assoc a = new Assoc();
        a.setLcardId(l);
        a.setRcardId(r);
        a.setAssoc(type);
        return a;
    }

    public void deleteAssoc(int id) {
        User user = getCurrentUser();//take user
        boolean isAdmin = user.getRole() == UserRole.ADMIN;
        Assoc assoc = assocRepository.findById(id).orElseThrow(() -> new AssocNotFoundException(String.valueOf(id)));

        int lcardid = assoc.getLcardId();
        int rcardid = assoc.getRcardId();

        if (!isAdmin &&
                !(cardRepository.findById(lcardid).get().getCreatedBy() == user.getUserId() &&
                        cardRepository.findById(rcardid).get().getCreatedBy() == user.getUserId()
                )
        ) {
            throw new AccessDeniedException("Can not associate cards with different users!");
        }

        Assoc assocInv = getAssoc(lcardid, rcardid);
        assocRepository.delete(assoc);
        assocRepository.delete(assocInv);
    }

    @Override
    public List<AssocDto> getCardAssocs(int cardId) {
        return assocRepository.findAssociationsAsDto(cardId);
    }

    @Override
    public Collection<Assoc> getCardAssocsByType(int cardId, AssocType assocType) {
        return assocRepository.findByCardIdAndAssocType(cardId, assocType);
    }

    public Assoc getAssoc(Integer rcardId, Integer lcardId) {
        Optional<Assoc> assocOptional = assocRepository.findByRcardIdAndLcardId(rcardId, lcardId);
        return assocOptional.orElseThrow(() -> new AssocNotFoundException("Association not found for rcardId: " + rcardId + " and lcardId: " + lcardId));

    }

    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext(
        ).getAuthentication().getPrincipal();
    }
}
