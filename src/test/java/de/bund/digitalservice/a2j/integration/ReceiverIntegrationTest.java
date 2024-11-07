package de.bund.digitalservice.a2j.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.fitko.fitconnect.api.domain.model.callback.NewSubmissionsCallback;
import dev.fitko.fitconnect.api.domain.model.submission.SubmissionForPickup;
import dev.fitko.fitconnect.api.domain.subscriber.ReceivedSubmission;
import dev.fitko.fitconnect.api.domain.validation.ValidationResult;
import dev.fitko.fitconnect.client.SenderClient;
import dev.fitko.fitconnect.client.SubscriberClient;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Tag("integration")
class ReceiverIntegrationTest {
  @LocalServerPort private int port;
  @Autowired private TestRestTemplate restTemplate;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private SubscriberClient subscriberClient;
  @MockBean private SenderClient senderClient;

  @Mock private SubmissionForPickup submissionForPickup;
  @Mock private ReceivedSubmission receivedSubmission;

  private HttpEntity<String> request;

  @BeforeEach
  void setup() throws JsonProcessingException {
    when(subscriberClient.requestSubmission(any(SubmissionForPickup.class)))
        .thenReturn(receivedSubmission);

    HttpHeaders headers = new HttpHeaders();
    headers.set("callback-authentication", "hmac");
    headers.set("callback-timestamp", "1234567890");
    headers.setContentType(MediaType.APPLICATION_JSON);

    NewSubmissionsCallback callback =
        new NewSubmissionsCallback("test", List.of(submissionForPickup));

    request = new HttpEntity<>(objectMapper.writeValueAsString(callback), headers);
  }

  @Test
  void shouldSecureCallbackWithAuthorization() {
    when(senderClient.validateCallback(any(), any(), any(), any()))
        .thenReturn(ValidationResult.ok());

    ResponseEntity<String> response =
        restTemplate.exchange(
            "http://localhost:" + port + "/callbacks/fit-connect",
            HttpMethod.POST,
            request,
            String.class);

    assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
  }

  @Test
  void shouldSecureCallbackWithoutAuthorization() {
    when(senderClient.validateCallback(any(), any(), any(), any()))
        .thenReturn(ValidationResult.error("invalid callback"));

    ResponseEntity<String> response =
        restTemplate.exchange(
            "http://localhost:" + port + "/callbacks/fit-connect",
            HttpMethod.POST,
            request,
            String.class);

    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode().value());
  }
}
