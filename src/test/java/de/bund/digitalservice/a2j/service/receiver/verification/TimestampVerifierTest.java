package de.bund.digitalservice.a2j.service.receiver.verification;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class TimestampVerifierTest {
  private TimestampVerifier verifier;

  @BeforeEach
  void setup() {
    this.verifier = new TimestampVerifier();
  }

  @Test
  void testVerify_withValidTimestamp() {
    long currentTime = Instant.now().getEpochSecond();
    String validTimestamp = String.valueOf(currentTime);

    assertTrue(verifier.verify(validTimestamp), "Callback should be valid with current timestamp");
  }

  @ParameterizedTest
  @ValueSource(strings = {"invalid", "", "-12334554121", "99999999999999999"})
  void testVerify_withMalformedTimestamp(String timestamp) {
    assertFalse(verifier.verify(timestamp));
  }

  @ParameterizedTest
  @ValueSource(longs = {301, 400, 6000})
  void testVerify_withOldTimestamp(long offset) {
    long currentTime = Instant.now().getEpochSecond();
    String timestamp = String.valueOf(currentTime - offset);
    assertFalse(verifier.verify(timestamp));
  }
}
