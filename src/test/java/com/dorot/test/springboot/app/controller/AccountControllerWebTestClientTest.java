package com.dorot.test.springboot.app.controller;

import com.dorot.test.springboot.app.dto.TransferDto;
import com.dorot.test.springboot.app.model.Account;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;

//La prueba unitaria levanta la aplicacion en un puerto random en un
// servidor real para consumir la API
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    @Order(1)
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
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody() //Por defecto es de tipo byte[]
                .consumeWith(r -> {
                    try {
                        JsonNode json = mapper.readTree(r.getResponseBody());
                        assertEquals("Transfer completed successfully", json.path("message").asText());
                        assertEquals(1L, json.path("transfer").path("sourceAccountId").asLong());
                        assertEquals(LocalDate.now().toString(), json.path("date").asText());
                        assertEquals("100", json.path("transfer").path("amount").asText());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .jsonPath("$.message").isNotEmpty()
                .jsonPath("$.message").value(is("Transfer completed successfully"))
                .jsonPath("$.message").value(v -> assertEquals("Transfer completed successfully", v))
                .jsonPath("$.message").isEqualTo("Transfer completed successfully")
                .jsonPath("$.transfer.sourceAccountId").isEqualTo(dto.getSourceAccountId())
                .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
                .json(mapper.writeValueAsString(response));
    }

    @Test
    @Order(2)
    void accountDetailTest() throws JsonProcessingException {
        Account account = new Account(1L, "Andres", new BigDecimal("900"));

        client.get().uri("http://localhost:8080/api/accounts/1").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.person").isEqualTo("Andres")
                .jsonPath("$.balance").isEqualTo(900)
                .json(mapper.writeValueAsString(account));
    }

    @Test
    @Order(3)
    void accountDetailTest2() {
        client.get().uri("http://localhost:8080/api/accounts/2").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Account.class)
                .consumeWith(r -> {
                   Account account = r.getResponseBody();
                   assertNotNull(account);
                   assertEquals("Jhon", account.getPerson());
                   assertEquals("2100.00", account.getBalance().toPlainString());
                });
    }

    @Test
    @Order(4)
    void listAccountsTest() {
        client.get().uri("http://localhost:8080/api/accounts").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].person").isEqualTo("Andres")
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].balance").isEqualTo(900)
                .jsonPath("$[1].person").isEqualTo("Jhon")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].balance").isEqualTo(2100)
                .jsonPath("$").isArray()
                .jsonPath("$").value(hasSize(2));
    }

    @Test
    @Order(5)
    void listAccountsTest2() {
        client.get().uri("http://localhost:8080/api/accounts").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Account.class)
                .consumeWith(r -> {
                    List<Account> accounts = r.getResponseBody();
                    assertNotNull(accounts);
                    assertEquals(2, accounts.size());
                    assertEquals(1L, accounts.get(0).getId());
                    assertEquals("Andres", accounts.get(0).getPerson());
                    assertEquals("900.00", accounts.get(0).getBalance().toPlainString());
                    assertEquals(2L, accounts.get(1).getId());
                    assertEquals("Jhon", accounts.get(1).getPerson());
                    assertEquals("2100.00", accounts.get(1).getBalance().toPlainString());
                })
                .hasSize(2)
                .value(hasSize(2));
    }

    @Test
    @Order(6)
    void saveAccountTest() {
        // Given
        Account account = new Account(null, "Pepe", new BigDecimal("3000"));

        // When
        client.post().uri("http://localhost:8080/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(account)
                .exchange()
                // Then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(3)
                .jsonPath("$.person").isEqualTo("Pepe")
                .jsonPath("$.person").value(is("Pepe"))
                .jsonPath("$.balance").isEqualTo(3000);

    }

    @Test
    @Order(7)
    void saveAccountTest2() {
        // Given
        Account account = new Account(null, "Pepa", new BigDecimal("3500"));

        // When
        client.post().uri("http://localhost:8080/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(account)
                .exchange()
                // Then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Account.class)
                .consumeWith(res -> {
                    Account c = res.getResponseBody();
                    assertNotNull(c);
                    assertEquals(4L, c.getId());
                    assertEquals("Pepa", c.getPerson());
                    assertEquals("3500", c.getBalance().toPlainString());
                });
    }

    @Test
    @Order(8)
    void deleteAccountTest() {
        client.get().uri("http://localhost:8080/api/accounts")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Account.class)
                .hasSize(4);
        client.delete().uri("http://localhost:8080/api/accounts/3")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
        client.get().uri("http://localhost:8080/api/accounts")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Account.class)
                .hasSize(3);
        client.get().uri("http://localhost:8080/api/accounts/3").exchange()
                //.expectStatus().is5xxServerError();
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }
}