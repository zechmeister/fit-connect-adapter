package de.bund.digitalservice.a2j.service.receiver;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CallbackVerificationServiceTest {
  private CallbackVerificationService service;

  @BeforeEach
  void setup() {
    this.service = new CallbackVerificationService();
  }

  @Test
  void testValidCallback_withValidTimestamp() {
    long currentTime = Instant.now().getEpochSecond();
    String validTimestamp = String.valueOf(currentTime);

    assertTrue(
        service.isValidCallback("auth", validTimestamp, "body"),
        "Callback should be valid with current timestamp");
  }

  @ParameterizedTest
  @ValueSource(strings = {"invalid", "", "-12334554121", "99999999999999999"})
  void testValidCallback_withMalformedTimestamp(String timestamp) {
    assertFalse(service.isValidCallback("auth", timestamp, "body"));
  }

  @ParameterizedTest
  @ValueSource(longs = {301, 400, 6000})
  void testValidCallback_withOldTimestamp(long offset) {
    long currentTime = Instant.now().getEpochSecond();
    String timestamp = String.valueOf(currentTime - offset);
    assertFalse(service.isValidCallback("auth", timestamp, "body"));
  }
}
