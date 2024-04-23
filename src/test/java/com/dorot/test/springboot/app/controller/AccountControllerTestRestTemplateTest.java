package com.dorot.test.springboot.app.controller;

import com.dorot.test.springboot.app.dto.TransferDto;
import com.dorot.test.springboot.app.model.Account;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration_rt")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerTestRestTemplateTest {

    @Autowired
    private TestRestTemplate client;

    private ObjectMapper mapper;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    void transferTest() throws JsonProcessingException {
        // Given
        TransferDto dto = new TransferDto();
        dto.setAmount(new BigDecimal("100"));
        dto.setSourceAccountId(1L);
        dto.setDestinationAccountId(2L);
        dto.setBankId(1L);

        // When
        ResponseEntity<String> response = client
                .postForEntity(createUri("/api/accounts/transfer"), dto, String.class);
        String json = response.getBody();
        System.out.println(json);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(json);
        assertTrue(json.contains("Transfer completed successfully"));
        assertTrue(json.contains("{\"sourceAccountId\":1,\"destinationAccountId\":2,\"amount\":100,\"bankId\":1}"));

        JsonNode jsonNode = mapper.readTree(json);
        assertEquals("Transfer completed successfully", jsonNode.path("message").asText());
        assertEquals(LocalDate.now().toString(), jsonNode.path("date").asText());
        assertEquals("100", jsonNode.path("transfer").path("amount").asText());
        assertEquals(1L, jsonNode.path("transfer").path("sourceAccountId").asLong());

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("date", LocalDate.now().toString());
        responseMap.put("status", "OK");
        responseMap.put("message", "Transfer completed successfully");
        responseMap.put("transfer", dto);

        assertEquals(mapper.writeValueAsString(responseMap), json);
    }

    private String createUri(String uri) {
        return "http://localhost:" + port + uri;
    }

    @Test
    @Order(2)
    void accountDetailsTest() {
        ResponseEntity<Account> response = client.getForEntity(createUri("/api/accounts/1"),
                Account.class);
        Account account = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(account);
        assertEquals(1L, account.getId());
        assertEquals("Andres", account.getPerson());
        assertEquals("900.00", account.getBalance().toPlainString());
        assertEquals(new Account(1L, "Andres", new BigDecimal("900.00")), account);
    }

    @Test
    @Order(3)
    void listAccountsTest() throws JsonProcessingException {
        ResponseEntity<Account[]> response = client.getForEntity(createUri("/api/accounts"),
                Account[].class);
        List<Account> accounts = Arrays.asList(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(2, accounts.size());
        assertEquals(1L, accounts.get(0).getId());
        assertEquals("Andres", accounts.get(0).getPerson());
        assertEquals("900.00", accounts.get(0).getBalance().toPlainString());
        assertEquals(2L, accounts.get(1).getId());
        assertEquals("Jhon", accounts.get(1).getPerson());
        assertEquals("2100.00", accounts.get(1).getBalance().toPlainString());

        JsonNode json = mapper.readTree(mapper.writeValueAsString(accounts));
        assertEquals(1L, json.get(0).path("id").asLong());
        assertEquals("Andres", json.get(0).path("person").asText());
        assertEquals("900.0", json.get(0).path("balance").asText());
        assertEquals(2L, json.get(1).path("id").asLong());
        assertEquals("Jhon", json.get(1).path("person").asText());
        assertEquals("2100.0", json.get(1).path("balance").asText());
    }

    @Test
    @Order(4)
    void saveAccountTest() {
        Account account = new Account(null, "Pepe", new BigDecimal("3600"));
        ResponseEntity<Account> response = client.postForEntity(createUri("/api/accounts"),
                account, Account.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        Account createdAccount = response.getBody();

        assertNotNull(createdAccount);
        assertEquals(3L, createdAccount.getId());
        assertEquals("Pepe", createdAccount.getPerson());
        assertEquals("3600", createdAccount.getBalance().toPlainString());
    }

    @Test
    @Order(5)
    void deleteAccountTest() {
        ResponseEntity<Account[]> response = client.getForEntity(createUri("/api/accounts"),
                Account[].class);
        List<Account> accounts = Arrays.asList(response.getBody());

        assertEquals(3, accounts.size());

        //client.delete(createUri("/api/accounts/3"));
        Map<String, Long> pathVariable = new HashMap<>();
        pathVariable.put("id", 3L);
        ResponseEntity<Void> exchange = client.exchange(createUri("/api/accounts/{id}"),
                HttpMethod.DELETE, null, Void.class, pathVariable);

        assertEquals(HttpStatus.NO_CONTENT, exchange.getStatusCode());
        assertFalse(exchange.hasBody());

        response = client.getForEntity(createUri("/api/accounts"), Account[].class);
        accounts = Arrays.asList(response.getBody());

        assertEquals(2, accounts.size());

        ResponseEntity<Account> accountEntity = client.getForEntity(createUri("/api/accounts/3"),
                Account.class);
        Account account = accountEntity.getBody();

        assertEquals(HttpStatus.NOT_FOUND, accountEntity.getStatusCode());
        assertFalse(accountEntity.hasBody());
    }
}