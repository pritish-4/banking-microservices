package com.example.banking.dto;

public record AccountResponseDTO(
        int id,
        String name,
        double balance
) { }
