package de.bund.digitalservice.a2j.service.subscriber;

import de.bund.digitalservice.a2j.service.egvp.client.EgvpClient;
import de.bund.digitalservice.a2j.service.egvp.client.EgvpClientException;
import de.bund.digitalservice.a2j.service.egvp.client.SendMessageRequest;
import dev.fitko.fitconnect.api.domain.model.submission.SubmissionForPickup;
import dev.fitko.fitconnect.api.domain.subscriber.ReceivedSubmission;
import dev.fitko.fitconnect.client.SubscriberClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FitConnectSubscriberService implements SubscriberService {

  private final SubscriberClient client;
  private final EgvpClient egvpClient;
  private final String testUserId;
  private final String testAntragPath;
  private final String testXjustizPath;
  private static final Logger logger = LoggerFactory.getLogger(FitConnectSubscriberService.class);

  public FitConnectSubscriberService(
      SubscriberClient client,
      EgvpClient egvpClient,
      @Value("${egvp.client.test.userId}") String userId,
      @Value("${egvp.client.test.antrag}") String testAntragPath,
      @Value("${egvp.client.test.xJustiz}") String testXjustizPath) {
    this.client = client;
    this.egvpClient = egvpClient;
    this.testUserId = userId;
    this.testAntragPath = testAntragPath;
    this.testXjustizPath = testXjustizPath;
  }

  public void pickUpSubmission(SubmissionForPickup submissionForPickup) throws EgvpClientException {
    ReceivedSubmission receivedSubmission = client.requestSubmission(submissionForPickup);
    logger.info("Submission requested. SubmissionId: {}", submissionForPickup.getSubmissionId());

    this.egvpClient.sendMessage(
        new SendMessageRequest(
            testUserId,
            testUserId,
            "mailbox",
            "testmessage_" + receivedSubmission.getCaseId(),
            testAntragPath,
            testXjustizPath));

    receivedSubmission.acceptSubmission();
    logger.info("Submission accepted. CaseId: {}", receivedSubmission.getCaseId());
  }
}
