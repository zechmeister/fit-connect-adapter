package de.bund.digitalservice.a2j.controller;

import de.bund.digitalservice.a2j.service.egvp.client.EgvpClientException;
import de.bund.digitalservice.a2j.service.subscriber.SubscriberService;
import dev.fitko.fitconnect.api.domain.model.callback.NewSubmissionsCallback;
import dev.fitko.fitconnect.api.domain.model.submission.SubmissionForPickup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubscriberController {

  private final SubscriberService service;

  public SubscriberController(SubscriberService service) {
    this.service = service;
  }

  private static final Logger logger = LoggerFactory.getLogger(SubscriberController.class);

  @PostMapping("callbacks/fit-connect")
  public void newSubmission(@RequestBody NewSubmissionsCallback callback) {
    for (SubmissionForPickup submissionForPickup : callback.getSubmissions()) {
      try {
        service.pickUpSubmission(submissionForPickup);
      } catch (EgvpClientException e) {
        logger.error("unable to propagate submission {}", submissionForPickup.getSubmissionId(), e);
      }
    }
  }
}
