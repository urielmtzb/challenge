package com.mb3.challenge.controller;

import com.mb3.challenge.dto.PetRequest;
import com.mb3.challenge.dto.PetResponse;
import com.mb3.challenge.exception.ServiceException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pet")
@Tag(name = "PetController", description = "Pet Service .")
public interface PetControllerI {

    @RequestMapping(value = "", method = RequestMethod.POST)
    @Operation(summary = "Create pet")
    ResponseEntity<PetResponse> create(@RequestBody PetRequest petRequest) throws ServiceException;

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    @Operation(summary = "Find one pet")
    ResponseEntity<PetResponse> findOne(
            @PathVariable(name = "id", required = true) Long id)
            throws ServiceException;
}
