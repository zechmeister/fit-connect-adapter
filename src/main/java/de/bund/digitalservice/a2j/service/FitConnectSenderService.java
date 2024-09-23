package de.bund.digitalservice.a2j.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.fitko.fitconnect.api.config.ApplicationConfig;
import dev.fitko.fitconnect.api.domain.model.submission.SentSubmission;
import dev.fitko.fitconnect.api.domain.sender.SendableSubmission;
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
            .setServiceType("urn:de:fim:leika:leistung:99001001000000", "FIT Connect Demo")
            .setJsonData(
                buildJSON(message),
                URI.create("https://schema.fitko.de/fim/s00000096_1.0.schema.json"))
            .build();

    SentSubmission sentSubmission = client.send(submission);

    System.out.println("helllooo" + sentSubmission.toString());

    return "Tried to send " + message;
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
