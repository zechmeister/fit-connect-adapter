package de.bund.digitalservice.a2j.service.sender;

import dev.fitko.fitconnect.api.domain.model.submission.SentSubmission;
import dev.fitko.fitconnect.api.domain.sender.SendableSubmission;
import dev.fitko.fitconnect.api.exceptions.client.FitConnectSenderException;
import dev.fitko.fitconnect.client.SenderClient;
import java.net.URI;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FitConnectSenderService implements SenderService {
  private final SenderClient client;
  private final String destinationUuid;
  private final String serviceUrn;
  private final String serviceName;
  private final String jsonUri;
  private final Logger logger = LoggerFactory.getLogger(FitConnectSenderService.class);

  public FitConnectSenderService(
      SenderClient senderClient,
      @Value("${fitConnect.submission.destination}") String destinationUuid,
      @Value("${fitConnect.submission.serviceType.urn}") String serviceUrn,
      @Value("${fitConnect.submission.serviceType.name}") String serviceName,
      @Value("${fitConnect.submission.jsonUri}") String jsonUri) {
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
      logger.info("Submission sent, caseId: " + sentSubmission.getCaseId());
      return "submission sent, caseId: " + sentSubmission.getCaseId();
    } catch (FitConnectSenderException e) {
      logger.error("failed to submit request: " + e.getMessage());
      return "failed to submit: " + e.getMessage();
    }
  }
}
