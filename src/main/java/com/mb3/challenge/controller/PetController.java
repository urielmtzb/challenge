package com.mb3.challenge.controller;

import com.mb3.challenge.dto.PetRequest;
import com.mb3.challenge.dto.PetResponse;
import com.mb3.challenge.exception.ServiceException;
import com.mb3.challenge.service.PetServiceI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
class PetController implements PetControllerI {

    private final PetServiceI petService;

    public PetController(PetServiceI petService) {
        this.petService = petService;
    }

    @Override
    public ResponseEntity<PetResponse> create(PetRequest petRequest) throws ServiceException {
        PetResponse petResponse = petService.create(petRequest);
        return new ResponseEntity<>(petResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<PetResponse> findOne(Long id) throws ServiceException {
        PetResponse petResponse = petService.findOne(id);
        return new ResponseEntity<>(petResponse, HttpStatus.OK);
    }
}
