package de.bund.digitalservice.a2j.service.egvp.client;

public record ResponseError(String responseCode, String errorDetail, String errorDescription) {}
