package de.bund.digitalservice.a2j.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.fitko.fitconnect.api.config.ApplicationConfig;
import dev.fitko.fitconnect.api.domain.model.submission.SentSubmission;
import dev.fitko.fitconnect.api.domain.sender.SendableSubmission;
import dev.fitko.fitconnect.api.exceptions.client.FitConnectSenderException;
import dev.fitko.fitconnect.client.SenderClient;
import dev.fitko.fitconnect.client.bootstrap.ClientFactory;
import java.net.URI;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class FitConnectSenderService implements SenderService {
  private final SenderClient client;

  public FitConnectSenderService(ApplicationConfig config) {
    this.client = ClientFactory.createSenderClient(config);
  }

  @Override
  public String sendMessage(String message) {

    SendableSubmission submission =
        SendableSubmission.Builder()
            .setDestination(UUID.fromString("89126fd7-1069-46f1-9cdc-152037db95a9"))
            .setServiceType("urn:de:fim:leika:leistung:99400048079000", "Simple Dummy Service")
            .setJsonData(buildJSON(message), URI.create("urn:de:fim:leika:leistung:99400048079000"))
            .build();

    try {
      SentSubmission sentSubmission = client.send(submission);
      return "sent submission: " + sentSubmission.toString();
    } catch (FitConnectSenderException e) {
      return "failed to submit: " + e.getMessage();
    }
  }

  private String buildJSON(String message) {
    ObjectMapper mapper = new ObjectMapper();

    ObjectNode rootNode = mapper.createObjectNode();
    rootNode.put("data", message);

    try {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    } catch (JsonProcessingException e) {
      return "{\"error\":\"Failed to build JSON\"}";
    }
  }
}
