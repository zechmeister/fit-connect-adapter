package de.bund.digitalservice.a2j.service.subscriber;

import de.bund.digitalservice.a2j.service.egvp.EgvpOutboxService;
import de.bund.digitalservice.a2j.service.egvp.client.EgvpClientException;
import de.bund.digitalservice.a2j.service.egvp.client.SendMessageRequest;
import dev.fitko.fitconnect.api.domain.model.submission.SubmissionForPickup;
import dev.fitko.fitconnect.api.domain.subscriber.ReceivedSubmission;
import dev.fitko.fitconnect.client.SubscriberClient;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FitConnectSubscriberService implements SubscriberService {

  private final SubscriberClient client;
  private final EgvpOutboxService egvpService;
  private final String testUserId;
  private static final Logger logger = LoggerFactory.getLogger(FitConnectSubscriberService.class);

  public FitConnectSubscriberService(
      SubscriberClient client,
      EgvpOutboxService egvpService,
      @Value("${egvp.client.testUserId}") String userId) {
    this.client = client;
    this.egvpService = egvpService;
    this.testUserId = userId;
  }

  public void pickUpSubmission(SubmissionForPickup submissionForPickup) throws EgvpClientException {
    ReceivedSubmission receivedSubmission = client.requestSubmission(submissionForPickup);
    logger.info("Submission requested. SubmissionId: {}", submissionForPickup.getSubmissionId());

    this.egvpService.sendMessage(
        new SendMessageRequest(
            testUserId,
            testUserId,
            "mailbox?",
            "testmessage_" + receivedSubmission.getCaseId(),
            Objects.requireNonNull(
                    this.getClass().getClassLoader().getResource("test/hello_world.pdf"))
                .getPath(),
            Objects.requireNonNull(
                    this.getClass().getClassLoader().getResource("test/xjustiz_nachricht.xml"))
                .getPath()));

    receivedSubmission.acceptSubmission();
    logger.info("Submission accepted. CaseId: {}", receivedSubmission.getCaseId());
  }
}
