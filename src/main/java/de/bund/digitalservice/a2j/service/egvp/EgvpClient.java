package de.bund.digitalservice.a2j.service.egvp;

import de.bund.digitalservice.a2j.service.egvp.DTO.*;
import java.util.Objects;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class EgvpClient {
  private final RestTemplate client;

  public EgvpClient(RestTemplate client) {
    this.client = client;
  }

  public GetVersionResponse getVersion() throws EgvpClientException {
    try {
      return this.client.getForEntity("/getVersion", GetVersionResponse.class).getBody();
    } catch (HttpClientErrorException e) {
      throw parseException(e);
    } catch (RestClientException e) {
      throw new EgvpClientException(e.getMessage());
    }
  }

  public SendMessageResponse sendMessage(SendMessageRequest request) throws EgvpClientException {
    try {
      return this.client
          .postForEntity("/sendMessage", request, SendMessageResponse.class)
          .getBody();
    } catch (HttpClientErrorException e) {
      throw parseException(e);
    } catch (RestClientException e) {
      throw new EgvpClientException(e.getMessage());
    }
  }

  public MessageDeliveryStatusResponse checkMessageStatus(String customId) {

    try {
      return this.client.getForObject(
          "/getMessageDeliveryStatus/{customId}", MessageDeliveryStatusResponse.class, customId);
    } catch (HttpClientErrorException e) {
      throw parseException(e);
    } catch (RestClientException e) {
      throw new EgvpClientException(e.getMessage());
    }
  }

  private EgvpClientException parseException(HttpClientErrorException e) {
    ResponseError re = e.getResponseBodyAs(ResponseError.class);
    if (Objects.isNull(re)) {
      return new EgvpClientException(e.getMessage());
    }
    return new EgvpClientException(re.responseCode());
  }
}
