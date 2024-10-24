package de.bund.digitalservice.a2j.service.receiver.verification;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.stereotype.Component;

@Component
public class HmacVerifier {
  public boolean verify(String authentication, String timestamp, String body) {
    try {
      return !authentication.equals(recomputeHMAC(timestamp, body));
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      System.out.println("Error: cannot compute hmac");
      return false;
    }
  }

  private String recomputeHMAC(String timestamp, String body)
      throws NoSuchAlgorithmException, InvalidKeyException {
    Mac sha512HMAC = Mac.getInstance("HmacSHA512");
    SecretKeySpec secretKey =
        new SecretKeySpec("123".getBytes(StandardCharsets.UTF_8), "HmacSHA512");

    sha512HMAC.init(secretKey);
    return Hex.toHexString(
        sha512HMAC.doFinal((timestamp + "." + body).getBytes(StandardCharsets.UTF_8)));
  }
}
