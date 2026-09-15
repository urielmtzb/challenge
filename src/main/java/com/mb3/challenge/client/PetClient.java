package com.mb3.challenge.client;

import com.mb3.challenge.dto.PetRequest;
import com.mb3.challenge.dto.PetResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "petstore-client", url = "${petstore.base-url}")
public interface PetClient {

    @GetMapping("/pet/{id}")
    PetResponse getPetById(@PathVariable("id") Long id);

    @PostMapping("/pet")
    PetResponse addPet(@RequestBody PetRequest petRequest);
}
