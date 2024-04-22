package com.dorot.test.springboot.app.controller;

import com.dorot.test.springboot.app.dto.TransferDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;

//La prueba unitaria levanta la aplicacion en un puerto random en un
// servidor real para consumir la API
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerWebTestClientTest {

    @Autowired
    private WebTestClient client;

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    void transferTest() throws Exception {
        // Given
        TransferDto dto = new TransferDto();
        dto.setSourceAccountId(1L);
        dto.setDestinationAccountId(2L);
        dto.setBankId(1L);
        dto.setAmount(new BigDecimal("100"));

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("message", "Transfer completed successfully");
        response.put("transfer", dto);

        // When
        client.post().uri("http://localhost:8080/api/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                // Then
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.message").isNotEmpty()
                    .jsonPath("$.message").value(is("Transfer completed successfully"))
                    .jsonPath("$.message").value(v -> assertEquals("Transfer completed successfully", v))
                    .jsonPath("$.message").isEqualTo("Transfer completed successfully")
                    .jsonPath("$.transfer.sourceAccountId").isEqualTo(dto.getSourceAccountId())
                    .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
                    .json(mapper.writeValueAsString(response));
    }
}