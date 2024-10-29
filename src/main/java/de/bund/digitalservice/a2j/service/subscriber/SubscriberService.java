package de.bund.digitalservice.a2j.service.subscriber;

import dev.fitko.fitconnect.api.domain.model.submission.SubmissionForPickup;

public interface SubscriberService {

  void pickUpSubmission(SubmissionForPickup submissionForPickup);
}
