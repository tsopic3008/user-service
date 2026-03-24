package com.tscore.dto;

public record CredentialRepresentation(
        String type,
        String value,
        boolean temporary
) {}
