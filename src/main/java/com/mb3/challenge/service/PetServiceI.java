package com.mb3.challenge.service;

import com.mb3.challenge.dto.PetRequest;
import com.mb3.challenge.dto.PetResponse;
import com.mb3.challenge.exception.ServiceException;

public interface PetServiceI {
    public PetResponse create (PetRequest petRequest) throws ServiceException;
    public PetResponse findOne(Long id) throws ServiceException;
}
