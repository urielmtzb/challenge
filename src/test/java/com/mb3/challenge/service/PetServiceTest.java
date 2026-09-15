package com.mb3.challenge.service;

import com.mb3.challenge.client.PetClient;
import com.mb3.challenge.dto.PetRequest;
import com.mb3.challenge.dto.PetResponse;
import com.mb3.challenge.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetClient petClient;

    @InjectMocks
    private PetService petService;

    private PetRequest petRequest;
    private PetResponse petFromApi;

    @BeforeEach
    void setUp() {
        petRequest = PetRequest.builder()
                .id(1L)
                .name("Firulais")
                .status("available")
                .build();

        petFromApi = PetResponse.builder()
                .id(1L)
                .name("Firulais")
                .status("available")
                .build();
    }

    // ─── create()

    @Test
    @DisplayName("create() debe llamar a petClient.addPet con el request correcto")
    void create_shouldCallClientWithRequest() throws ServiceException {
        when(petClient.addPet(any(PetRequest.class))).thenReturn(petFromApi);

        petService.create(petRequest);

        verify(petClient, times(1)).addPet(petRequest);
    }

    @Test
    @DisplayName("create() debe retornar una respuesta con transactionId en formato UUIDv4")
    void create_shouldSetTransactionIdAsUuidV4() throws ServiceException {
        when(petClient.addPet(any())).thenReturn(petFromApi);

        PetResponse result = petService.create(petRequest);

        assertThat(result.getTransactionId()).isNotNull();
        assertThat(result.getTransactionId()).matches(
                "^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$"
        );
    }

    @Test
    @DisplayName("create() debe retornar una respuesta con dateCreated no nulo")
    void create_shouldSetDateCreated() throws ServiceException {
        when(petClient.addPet(any())).thenReturn(petFromApi);

        PetResponse result = petService.create(petRequest);

        assertThat(result.getDateCreated()).isNotNull();
    }

    @Test
    @DisplayName("create() debe preservar los campos del API (id, name, status) sin modificarlos")
    void create_shouldPreserveApiFields() throws ServiceException {
        when(petClient.addPet(any())).thenReturn(petFromApi);

        PetResponse result = petService.create(petRequest);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Firulais");
        assertThat(result.getStatus()).isEqualTo("available");
    }

    @Test
    @DisplayName("create() cada llamada debe generar un transactionId único")
    void create_shouldGenerateUniqueTransactionIds() throws ServiceException {
        when(petClient.addPet(any()))
                .thenReturn(PetResponse.builder().id(1L).name("Firulais").status("available").build())
                .thenReturn(PetResponse.builder().id(1L).name("Firulais").status("available").build());

        PetResponse r1 = petService.create(petRequest);
        PetResponse r2 = petService.create(petRequest);

        assertThat(r1.getTransactionId()).isNotEqualTo(r2.getTransactionId());
    }

    // ─── findOne()
    @Test
    @DisplayName("findOne() debe llamar a petClient.getPetById con el id correcto")
    void findOne_shouldCallClientWithId() throws ServiceException {
        when(petClient.getPetById(1L)).thenReturn(petFromApi);

        petService.findOne(1L);

        verify(petClient, times(1)).getPetById(1L);
    }

    @Test
    @DisplayName("findOne() debe retornar la respuesta del cliente sin modificarla")
    void findOne_shouldReturnClientResponseUnmodified() throws ServiceException {
        when(petClient.getPetById(1L)).thenReturn(petFromApi);

        PetResponse result = petService.findOne(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Firulais");
        assertThat(result.getStatus()).isEqualTo("available");
        assertThat(result.getTransactionId()).isNull();
        assertThat(result.getDateCreated()).isNull();
    }
}
