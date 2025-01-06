package de.bund.digitalservice.a2j.service.subscriber;

import de.bund.digitalservice.a2j.service.egvp.EgvpOutboxService;
import de.bund.digitalservice.a2j.service.egvp.client.EgvpClientException;
import de.bund.digitalservice.a2j.service.egvp.client.SendMessageRequest;
import dev.fitko.fitconnect.api.domain.model.submission.SubmissionForPickup;
import dev.fitko.fitconnect.api.domain.subscriber.ReceivedSubmission;
import dev.fitko.fitconnect.client.SubscriberClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FitConnectSubscriberService implements SubscriberService {

  private final SubscriberClient client;
  private final EgvpOutboxService egvpService;
  private static final Logger logger = LoggerFactory.getLogger(FitConnectSubscriberService.class);

  public FitConnectSubscriberService(SubscriberClient client, EgvpOutboxService egvpService) {
    this.client = client;
    this.egvpService = egvpService;
  }

  public void pickUpSubmission(SubmissionForPickup submissionForPickup) throws EgvpClientException {
    ReceivedSubmission receivedSubmission = client.requestSubmission(submissionForPickup);
    logger.info("Submission requested. SubmissionId: {}", submissionForPickup.getSubmissionId());

    String body = receivedSubmission.getDataAsString();
    logger.info("Submission body: {}", body);

    this.egvpService.sendMessage(
        new SendMessageRequest(
            "userId",
            "receiverId",
            "mailbox?",
            "subject",
            this.getClass().getClassLoader().getResource("test/hello_world.pdf").getPath(),
            this.getClass().getClassLoader().getResource("test/xjustiz_nachricht.xml").getPath()));

    receivedSubmission.acceptSubmission();
    logger.info("Submission accepted. CaseId: {}", receivedSubmission.getCaseId());
  }
}
