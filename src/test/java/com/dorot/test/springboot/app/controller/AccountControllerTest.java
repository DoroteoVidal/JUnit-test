package com.dorot.test.springboot.app.controller;

import com.dorot.test.springboot.app.dto.TransferDto;
import com.dorot.test.springboot.app.model.Account;
import com.dorot.test.springboot.app.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static com.dorot.test.springboot.app.Data.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AccountService accountService;
    ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    void accountDetailsTest() throws Exception {
        // Given
        when(accountService.findById(1L)).thenReturn(createAccount001().orElseThrow());

        // When
        mvc.perform(get("/api/accounts/1").contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.person").value("Andres"))
                .andExpect(jsonPath("$.balance").value("1000"));

        verify(accountService).findById(1L);
    }

    @Test
    void transferTest() throws Exception {
        // Given
        TransferDto dto = new TransferDto();
        dto.setSourceAccountId(1L);
        dto.setDestinationAccountId(2L);
        dto.setAmount(new BigDecimal("100"));
        dto.setBankId(1L);

        System.out.println(mapper.writeValueAsString(dto));

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("message", "Transfer completed successfully");
        response.put("transfer", dto);

        System.out.println(mapper.writeValueAsString(response));

        // When
        mvc.perform(post("/api/accounts/transfer").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                //Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.message").value("Transfer completed successfully"))
                .andExpect(jsonPath("$.transfer.sourceAccountId").value(dto.getSourceAccountId()))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void listAccountsTest() throws Exception {
        // Given
        List<Account> accounts = Arrays.asList(
                createAccount001().orElseThrow(),
                createAccount002().orElseThrow());
        when(accountService.findAll()).thenReturn(accounts);

        // When
        mvc.perform(get("/api/accounts").contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].person").value("Andres"))
                .andExpect(jsonPath("$.[1].person").value("Jhon"))
                .andExpect(jsonPath("$.[0].balance").value("1000"))
                .andExpect(jsonPath("$.[1].balance").value("2000"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(mapper.writeValueAsString(accounts)));
        verify(accountService).findAll();
    }

    @Test
    void saveAccountTest() throws Exception {
        // Given
        Account account = new Account(null, "Pepe", new BigDecimal("3000"));
        when(accountService.save(any())).then(invocation -> {
            Account a = invocation.getArgument(0);
            a.setId(3L);

            return a;
        });

        // When
        mvc.perform(post("/api/accounts").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(account)))
                // Then
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.person", is("Pepe")))
                .andExpect(jsonPath("$.balance", is(3000)));
        verify(accountService).save(any());
    }
}