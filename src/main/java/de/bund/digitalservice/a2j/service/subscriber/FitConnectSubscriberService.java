package de.bund.digitalservice.a2j.service.subscriber;

import dev.fitko.fitconnect.api.domain.model.submission.SubmissionForPickup;
import dev.fitko.fitconnect.api.domain.subscriber.ReceivedSubmission;
import dev.fitko.fitconnect.client.SubscriberClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FitConnectSubscriberService implements SubscriberService {

  private final SubscriberClient client;
  private static final Logger logger = LoggerFactory.getLogger(FitConnectSubscriberService.class);

  public FitConnectSubscriberService(SubscriberClient client) {
    this.client = client;
  }

  public void pickUpSubmission(SubmissionForPickup submissionForPickup) {
    ReceivedSubmission receivedSubmission = client.requestSubmission(submissionForPickup);
    logger.info("Submission requested. SubmissionId: " + submissionForPickup.getSubmissionId());

    receivedSubmission.acceptSubmission();
    logger.info("Submission accepted. CaseId: " + receivedSubmission.getCaseId());
  }
}
