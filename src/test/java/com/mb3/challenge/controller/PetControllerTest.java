package com.mb3.challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mb3.challenge.dto.PetRequest;
import com.mb3.challenge.dto.PetResponse;
import com.mb3.challenge.exception.ServiceException;
import com.mb3.challenge.service.PetServiceI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PetControllerTest {

    /**
     * ControllerAdvice local para que las ServiceException se conviertan en error 500
     * dentro del contexto standalone de MockMvc.
     */
    @RestControllerAdvice
    static class TestExceptionHandler {
        @ExceptionHandler(ServiceException.class)
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        void handleServiceException() { /* retorna 500 con body vacío */ }
    }

    private MockMvc mockMvc;

    @Mock
    private PetServiceI petService;

    @InjectMocks
    private PetController petController;

    // findAndRegisterModules() auto-descubre JavaTimeModule vía SPI (jackson-datatype-jsr310)
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private PetRequest petRequest;
    private PetResponse petResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(petController)
                .setControllerAdvice(new TestExceptionHandler())
                .build();

        petRequest = PetRequest.builder()
                .id(1L)
                .name("Firulais")
                .status("available")
                .build();

        petResponse = PetResponse.builder()
                .id(1L)
                .name("Firulais")
                .status("available")
                .transactionId("550e8400-e29b-41d4-a716-446655440000")
                .dateCreated(LocalDateTime.of(2026, 9, 15, 14, 0, 0))
                .build();
    }

    // ─── POST /api/v1/pet
    @Test
    @DisplayName("POST /api/v1/pet debe retornar 201 CREATED con todos los campos")
    void create_shouldReturn201WithBody() throws Exception {
        when(petService.create(any(PetRequest.class))).thenReturn(petResponse);

        mockMvc.perform(post("/api/v1/pet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(petRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Firulais"))
                .andExpect(jsonPath("$.status").value("available"))
                .andExpect(jsonPath("$.transactionId").value("550e8400-e29b-41d4-a716-446655440000"))
                .andExpect(jsonPath("$.dateCreated").exists());
    }

    @Test
    @DisplayName("POST /api/v1/pet sin body debe retornar 400 BAD REQUEST")
    void create_withoutBody_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v1/pet")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // ─── GET /api/v1/pet/{id}
    @Test
    @DisplayName("GET /api/v1/pet/{id} debe retornar 200 OK con la mascota")
    void findOne_shouldReturn200WithBody() throws Exception {
        when(petService.findOne(eq(1L))).thenReturn(petResponse);

        mockMvc.perform(get("/api/v1/pet/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Firulais"))
                .andExpect(jsonPath("$.status").value("available"));
    }

    @Test
    @DisplayName("GET /api/v1/pet/{id} cuando el service lanza ServiceException debe retornar 500")
    void findOne_whenServiceThrows_shouldReturn500() throws Exception {
        when(petService.findOne(eq(99L))).thenThrow(new ServiceException("Pet not found"));

        mockMvc.perform(get("/api/v1/pet/{id}", 99L))
                .andExpect(status().isInternalServerError());
    }
}
