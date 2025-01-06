package de.bund.digitalservice.a2j.service.egvp.DTO;

public record SendMessageRequest(
    String receiverId,
    String bundIdMailbox,
    String subject,
    String attachmentFile,
    String xJustizFile) {}
