package de.bund.digitalservice.a2j.service.egvp.client;

public class EgvpClientException extends Exception {
  public EgvpClientException(String message) {
    super(message);
  }

  public EgvpClientException(String message, Throwable cause) {
    super(message, cause);
  }
}
