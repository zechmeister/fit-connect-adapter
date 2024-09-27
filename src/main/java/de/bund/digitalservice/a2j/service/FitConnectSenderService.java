package de.bund.digitalservice.a2j.service;

import dev.fitko.fitconnect.api.config.ApplicationConfig;
import dev.fitko.fitconnect.api.domain.model.submission.SentSubmission;
import dev.fitko.fitconnect.api.domain.sender.SendableSubmission;
import dev.fitko.fitconnect.api.exceptions.client.FitConnectSenderException;
import dev.fitko.fitconnect.client.SenderClient;
import dev.fitko.fitconnect.client.bootstrap.ClientFactory;
import java.net.URI;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FitConnectSenderService implements SenderService {
  private final SenderClient client;

  @Value("${submission.destination}")
  private String destinationUuid;

  @Value("${submission.serviceType.urn}")
  private String serviceUrn;

  @Value("${submission.serviceType.name}")
  private String serviceName;

  @Value("${submission.jsonUri}")
  private String jsonUri;

  public FitConnectSenderService(ApplicationConfig config) {
    this.client = ClientFactory.createSenderClient(config);
  }

  @Override
  public String submit(SubmitRequest submitRequest) {
    SendableSubmission submission =
        SendableSubmission.Builder()
            .setDestination(UUID.fromString(destinationUuid))
            .setServiceType(serviceUrn, serviceName)
            .setJsonData(
                DummyDataGenerator.generateDummyData(submitRequest.message()), URI.create(jsonUri))
            .build();

    try {
      SentSubmission sentSubmission = client.send(submission);
      return "submission sent, caseId: " + sentSubmission.getCaseId();
    } catch (FitConnectSenderException e) {
      return "failed to submit: " + e.getMessage();
    }
  }
}
