package de.bund.digitalservice.a2j.controller;

import de.bund.digitalservice.a2j.service.subscriber.SubscriberService;
import dev.fitko.fitconnect.api.domain.model.submission.SubmissionForPickup;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubscriberController {

  private final SubscriberService service;

  public SubscriberController(SubscriberService service) {
    this.service = service;
  }

  @PostMapping("callbacks/fit-connect")
  public void newSubmission(@RequestBody SubmissionForPickup submission) {
    service.pickUpSubmission(submission);
  }
}
