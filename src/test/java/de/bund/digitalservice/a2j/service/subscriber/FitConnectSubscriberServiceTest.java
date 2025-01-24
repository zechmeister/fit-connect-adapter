package de.bund.digitalservice.a2j.service.subscriber;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.a2j.service.egvp.client.EgvpClient;
import de.bund.digitalservice.a2j.service.egvp.client.EgvpClientException;
import de.bund.digitalservice.a2j.service.egvp.client.SendMessageRequest;
import dev.fitko.fitconnect.api.domain.model.submission.SubmissionForPickup;
import dev.fitko.fitconnect.api.domain.subscriber.ReceivedSubmission;
import dev.fitko.fitconnect.client.SubscriberClient;
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
  @MockBean EgvpClient egvpClient;

  @Mock ReceivedSubmission receivedSubmission;
  @Mock SubmissionForPickup submissionForPickup;

  @BeforeEach
  void setup() {
    this.service =
        new FitConnectSubscriberService(client, egvpClient, "testUserId", "path1", "path2");
  }

  @Test
  void testPickUpSubmission() throws EgvpClientException {
    when(client.requestSubmission(submissionForPickup)).thenReturn(receivedSubmission);
    UUID caseId = UUID.randomUUID();
    when(receivedSubmission.getSubmissionId()).thenReturn(UUID.randomUUID());
    when(receivedSubmission.getCaseId()).thenReturn(caseId);
    service.pickUpSubmission(submissionForPickup);

    SendMessageRequest expectedRequest =
        new SendMessageRequest(
            "testUserId", "testUserId", "mailbox", "testmessage_" + caseId, "path1", "path2");
    verify(egvpClient).sendMessage(expectedRequest);
    verify(client).requestSubmission(submissionForPickup);
    verify(receivedSubmission).acceptSubmission();
  }
}
