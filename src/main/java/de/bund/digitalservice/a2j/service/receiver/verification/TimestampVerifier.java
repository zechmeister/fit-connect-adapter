package de.bund.digitalservice.a2j.service.receiver.verification;

import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class TimestampVerifier {
  public boolean verify(String callbackTimestamp) {
    try {
      long timestamp = Long.parseLong(callbackTimestamp);
      long currentTime = Instant.now().getEpochSecond();

      return timestamp <= currentTime && (currentTime - timestamp) < 300;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
