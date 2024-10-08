package de.bund.digitalservice.a2j.service;

import dev.fitko.fitconnect.api.domain.model.submission.SentSubmission;
import dev.fitko.fitconnect.api.domain.sender.SendableSubmission;
import dev.fitko.fitconnect.api.exceptions.client.FitConnectSenderException;
import dev.fitko.fitconnect.client.SenderClient;
import java.net.URI;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FitConnectSenderService implements SenderService {
  private final SenderClient client;
  private String destinationUuid;
  private String serviceUrn;
  private String serviceName;
  private String jsonUri;

  public FitConnectSenderService(
      SenderClient senderClient,
      @Value("${submission.destination}") String destinationUuid,
      @Value("${submission.serviceType.urn}") String serviceUrn,
      @Value("${submission.serviceType.name}") String serviceName,
      @Value("${submission.jsonUri}") String jsonUri) {
    this.client = senderClient;
    this.destinationUuid = destinationUuid;
    this.serviceUrn = serviceUrn;
    this.serviceName = serviceName;
    this.jsonUri = jsonUri;
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
