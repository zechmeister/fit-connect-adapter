package de.bund.digitalservice.a2j.service.receiver.verification;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CallbackVerificationServiceTest {

  @Mock private TimestampVerifier timestampVerifier;
  @Mock private HmacVerifier hmacVerifier;

  private CallbackVerificationService service;

  @BeforeEach
  void setup() {
    service = new CallbackVerificationService(timestampVerifier, hmacVerifier);
  }

  @Test
  void testValidCallback_withValidCallback() {
    String timestamp = "validTimestamp";
    String authentication = "validAuthentication";
    String body = "body";

    when(timestampVerifier.verify(timestamp)).thenReturn(true);
    when(hmacVerifier.verify(authentication, timestamp, body)).thenReturn(true);

    assertTrue(service.isValidCallback(authentication, timestamp, body));
  }

  @Test
  void testValidCallback_withInvalidTimestamp() {
    String timestamp = "invalidTimestamp";
    String authentication = "validAuthentication";
    String body = "body";

    when(timestampVerifier.verify(timestamp)).thenReturn(false);
    lenient().when(hmacVerifier.verify(authentication, timestamp, body)).thenReturn(true);

    assertFalse(service.isValidCallback(authentication, timestamp, body));
  }

  @Test
  void testValidCallback_withInvalidHmac() {
    String timestamp = "validTimestamp";
    String authentication = "invalidAuthentication";
    String body = "body";

    when(timestampVerifier.verify(timestamp)).thenReturn(true);
    when(hmacVerifier.verify(authentication, timestamp, body)).thenReturn(false);

    assertFalse(service.isValidCallback(authentication, timestamp, body));
  }
}
