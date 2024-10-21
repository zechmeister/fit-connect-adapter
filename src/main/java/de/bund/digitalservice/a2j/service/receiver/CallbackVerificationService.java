package de.bund.digitalservice.a2j.service.receiver;

import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class CallbackVerificationService {
  public boolean isValidCallback(String callbackAuth, String callbackTimestamp, String body) {
    if (!verifyTimestamp(callbackTimestamp)) {
      System.out.println("Error: callback timestamp invalid");
      return false;
    }

    return true;
  }

  private boolean verifyTimestamp(String callbackTimestamp) {
    try {
      long timestamp = Long.parseLong(callbackTimestamp);
      long currentTime = Instant.now().getEpochSecond();

      return timestamp <= currentTime && (currentTime - timestamp) < 300;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
