package de.bund.digitalservice.a2j.service.subscriber;

import de.bund.digitalservice.a2j.service.egvp.client.EgvpClientException;
import dev.fitko.fitconnect.api.domain.model.submission.SubmissionForPickup;

public interface SubscriberService {

  void pickUpSubmission(SubmissionForPickup submissionForPickup) throws EgvpClientException;
}
