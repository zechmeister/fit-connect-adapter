package de.bund.digitalservice.a2j.service.sender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import dev.fitko.fitconnect.api.domain.model.submission.SentSubmission;
import dev.fitko.fitconnect.api.domain.sender.SendableSubmission;
import dev.fitko.fitconnect.api.exceptions.client.FitConnectSenderException;
import dev.fitko.fitconnect.client.SenderClient;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class FitConnectSenderServiceTest {
  @MockBean private SenderClient mockClient;
  @Mock private SentSubmission mockSentSubmission;
  private FitConnectSenderService service;

  @BeforeEach()
  void setup() {
    service =
        new FitConnectSenderService(
            mockClient,
            "e51fce69-e8c1-4f61-88d8-600d7ccd95a9",
            "urn:test:service",
            "test service",
            "https://example.com/json");
  }

  @Test
  void submitWithSuccess() {
    UUID mockCaseId = UUID.fromString("45ae11f6-8e01-47c3-90af-678ec59c4a01");
    when(mockSentSubmission.getCaseId()).thenReturn(mockCaseId);
    when(mockClient.send(any(SendableSubmission.class))).thenReturn(mockSentSubmission);

    String result = service.submit(new SubmitRequest("testRequest"));
    assertEquals("submission sent, caseId: " + mockCaseId.toString(), result);
  }

  @Test
  void submitWithoutSuccess() {
    String errorMessage = "test error";
    when(mockClient.send(any(SendableSubmission.class)))
        .thenThrow(new FitConnectSenderException(errorMessage));

    String result = service.submit(new SubmitRequest("testRequest"));
    assertEquals(result, "failed to submit: " + errorMessage);
  }
}
