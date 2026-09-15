package com.mb3.challenge.service;

import com.mb3.challenge.client.PetClient;
import com.mb3.challenge.dto.PetRequest;
import com.mb3.challenge.dto.PetResponse;
import com.mb3.challenge.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
class PetService implements PetServiceI {

    private final PetClient petClient;

    @Override
    public PetResponse create(PetRequest petRequest) throws ServiceException {
        log.info("Creando mascota -> id: {}, nombre: {}, status: {}",
                petRequest.getId(), petRequest.getName(), petRequest.getStatus());

        PetResponse pet = petClient.addPet(petRequest);

        // Enriquecer con campos generados en la capa service
        pet.setTransactionId(UUID.randomUUID().toString());
        pet.setDateCreated(LocalDateTime.now());

        log.info("Mascota creada exitosamente -> id: {}, nombre: {}, status: {}, transactionId: {}, dateCreated: {}",
                pet.getId(), pet.getName(), pet.getStatus(),
                pet.getTransactionId(), pet.getDateCreated());

        return pet;
    }

    @Override
    public PetResponse findOne(Long id) throws ServiceException {
        log.info("Buscando mascota con id: {}", id);

        PetResponse pet = petClient.getPetById(id);

        log.info("Mascota encontrada -> id: {}, nombre: {}, status: {}",
                pet.getId(), pet.getName(), pet.getStatus());

        return pet;
    }
}
