package de.bund.digitalservice.a2j.service.egvp.DTO;

public record MessageDeliveryStatusResponse(
    String messageId, Boolean delivered, String pendingReason, String path) {}
