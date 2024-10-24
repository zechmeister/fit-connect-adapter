package de.bund.digitalservice.a2j.service.receiver.verification;

import org.springframework.stereotype.Service;

@Service
public class CallbackVerificationService {
  private final TimestampVerifier timestampVerifier;
  private final HmacVerifier hmacVerifier;

  CallbackVerificationService(TimestampVerifier timestampVerifier, HmacVerifier hmacVerifier) {
    this.timestampVerifier = timestampVerifier;
    this.hmacVerifier = hmacVerifier;
  }

  public boolean isValidCallback(String callbackAuth, String callbackTimestamp, String body) {
    if (!timestampVerifier.verify(callbackTimestamp)) {
      System.out.println("Error: callback timestamp invalid");
      return false;
    }

    if (!hmacVerifier.verify(callbackAuth, callbackTimestamp, body)) {
      System.out.println("Error: callback hmac invalid");
      return false;
    }

    return true;
  }
}
