package com.logicea.cards.service.impl;

import com.logicea.cards.AssocAlreadyExistsException;
import com.logicea.cards.AssocNotFoundException;
import com.logicea.cards.CardNotFoundException;
import com.logicea.cards.dto.AssocDto;
import com.logicea.cards.entity.Assoc;
import com.logicea.cards.entity.Card;
import com.logicea.cards.mapper.UserDetailsMapper;
import com.logicea.cards.repository.AssocRepository;
import com.logicea.cards.repository.CardRepository;
import com.logicea.cards.service.AssocService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;


import java.util.ArrayList;
import java.util.List;

@Service
public class AssocServiceImpl implements AssocService {
    private final AssocRepository assocRepository;
    private final CardRepository cardRepository;

    public AssocServiceImpl(AssocRepository assocRepository, CardRepository cardRepository) {
        this.assocRepository = assocRepository;
        this.cardRepository = cardRepository;
    }



    public List<Integer> newAssoc(AssocDto assocDto) {
        Card lcard = cardRepository.findById(assocDto.lcardId())
                .orElseThrow(() -> new CardNotFoundException(assocDto.lcardId()));
        Card rcard = cardRepository.findById(assocDto.rcardId())
                .orElseThrow(() -> new CardNotFoundException(assocDto.rcardId()));
        UserDetailsMapper user = getCurrentUser();//take user
        boolean isAdmin = user.getAuthorities().stream() //take user's role
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean valid;
        if(!assocRepository.findByRcardId(assocDto.rcardId()).isPresent() & !assocRepository.findByLcardId(assocDto.lcardId()).isPresent()) {
            if (isAdmin) { //if is admin he can update any card
                valid = true;

            } else {
                boolean ownsLeftCard = lcard.getCreatedBy() == user.getUserId();
                boolean ownsRightCard = rcard.getCreatedBy() == user.getUserId();
                System.out.println("member newAssoc");
                if (!ownsLeftCard || !ownsRightCard) {
                    valid = false;
                    throw new AccessDeniedException("Can not associate cards with different users!");
                }
                valid = true;
            }
        }
        else{
            throw new AssocAlreadyExistsException("This association already exists!");
        }
        if(valid){
            Assoc entity1 = new Assoc();
            entity1.setLcardId(assocDto.lcardId());
            entity1.setAssoc(assocDto.assoc());
            entity1.setRcardId(assocDto.rcardId());
            Assoc savedEntity1 = assocRepository.save(entity1);
            Assoc entity2 = new Assoc();
            entity2.setLcardId(assocDto.rcardId());
            entity2.setAssoc(assocDto.assoc().getInverseAssoc());
            entity2.setRcardId(assocDto.lcardId());
            Assoc savedEntity2 = assocRepository.save(entity2);
            System.out.println("admin newAssoc");
            AssocDto savedentity1= new AssocDto(
                    savedEntity1.getId(),
                    savedEntity1.getLcardId(),
                    savedEntity1.getAssoc(),
                    savedEntity1.getRcardId()
            );
            AssocDto savedentity2 =new AssocDto(
                    savedEntity2.getId(),
                    savedEntity2.getLcardId(),
                    savedEntity2.getAssoc(),
                    savedEntity2.getRcardId()
            );
            List<Integer> savedIds = new ArrayList<>();
            savedIds.add(savedEntity1.getId());
            savedIds.add(savedEntity2.getId());
            return savedIds;
        }
        else{
            return List.of();
        }

    }


    public void deleteAssoc(int id){
        boolean valid;
        UserDetailsMapper user = getCurrentUser();//take user
        boolean isAdmin = user.getAuthorities().stream() //take user's role
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin){
            valid=true;
        }
        else{
            int lcardid=assocRepository.findById(id).get().getLcardId();
            int rcardid=assocRepository.findById(id).get().getRcardId();

            if (cardRepository.findById(lcardid).get().getCreatedBy()==user.getUserId() & cardRepository.findById(rcardid).get().getCreatedBy()==user.getUserId()) {
                valid=true;
            }
            else{
                throw new AccessDeniedException("Can not associate cards with different users!");
            }
        }
        if (valid){
            int lcardid=assocRepository.findById(id).get().getLcardId();
            int rcardid=assocRepository.findById(id).get().getRcardId();
            Assoc assoc=getAssocByRcard(rcardid,lcardid);
            Assoc assoc_inv=getAssocByRcard(lcardid,rcardid);
            assocRepository.delete(assoc);
            assocRepository.delete(assoc_inv);
        }
    }

    public Assoc getAssocByRcard(Integer rcardId,Integer lcardId) {
        Assoc assoc = assocRepository.findByRcardId(rcardId).get();
        if (assoc.getLcardId()==lcardId){
            return assoc;
        }
        else{
            throw new AssocNotFoundException(assoc.getId());
        }
    }

    private UserDetailsMapper getCurrentUser() {
        return (UserDetailsMapper) SecurityContextHolder.getContext(
        ).getAuthentication().getPrincipal();
    }

}
