package de.bund.digitalservice.a2j.service.egvp;

import de.bund.digitalservice.a2j.service.egvp.DTO.GetVersionResponse;
import de.bund.digitalservice.a2j.service.egvp.DTO.ResponseError;
import java.util.Objects;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class EgvpClient {
  private final RestTemplate client;

  public EgvpClient(RestTemplate client) {
    this.client = client;
  }

  public GetVersionResponse getVersion() throws EgvpClientException, HttpClientErrorException {
    try {
      return this.client.getForEntity("/getVersion", GetVersionResponse.class).getBody();
    } catch (HttpClientErrorException e) {
      ResponseError error = e.getResponseBodyAs(ResponseError.class);

      if (Objects.isNull(error)) {
        throw e;
      } else {
        throw new EgvpClientException(error.responseCode());
      }
    }
  }
}
