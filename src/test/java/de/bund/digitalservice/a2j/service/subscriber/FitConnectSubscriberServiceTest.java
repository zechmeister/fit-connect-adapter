package de.bund.digitalservice.a2j.service.subscriber;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.a2j.service.egvp.EgvpOutboxService;
import de.bund.digitalservice.a2j.service.egvp.client.EgvpClientException;
import dev.fitko.fitconnect.api.domain.model.submission.SubmissionForPickup;
import dev.fitko.fitconnect.api.domain.subscriber.ReceivedSubmission;
import dev.fitko.fitconnect.client.SubscriberClient;
import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class FitConnectSubscriberServiceTest {
  private SubscriberService service;

  @MockBean SubscriberClient client;
  @MockBean EgvpOutboxService egvpService;

  @Mock ReceivedSubmission receivedSubmission;
  @Mock SubmissionForPickup submissionForPickup;

  @BeforeEach
  void setup() {
    this.service = new FitConnectSubscriberService(client, egvpService);
  }

  @Test
  void testPickUpSubmission() throws IOException, EgvpClientException {
    when(client.requestSubmission(submissionForPickup)).thenReturn(receivedSubmission);
    when(receivedSubmission.getSubmissionId()).thenReturn(UUID.randomUUID());

    service.pickUpSubmission(submissionForPickup);

    verify(client).requestSubmission(submissionForPickup);
    verify(receivedSubmission).acceptSubmission();
  }
}
