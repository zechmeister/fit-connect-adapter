package de.bund.digitalservice.a2j.service.egvp.client;

public record MessageDeliveryStatusResponse(
    String messageId, Boolean delivered, String pendingReason, String path) {}
