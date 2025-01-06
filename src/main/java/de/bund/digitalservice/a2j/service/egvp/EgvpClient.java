package de.bund.digitalservice.a2j.service.egvp;

import de.bund.digitalservice.a2j.service.egvp.DTO.*;
import java.util.Objects;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class EgvpClient {
  private final RestTemplate client;

  public EgvpClient(RestTemplate client) {
    this.client = client;
  }

  public SendMessageResponse sendMessage(SendMessageRequest request) throws EgvpClientException {
    return invoke(
        () ->
            this.client
                .postForEntity("/sendMessage", request, SendMessageResponse.class)
                .getBody());
  }

  public MessageDeliveryStatusResponse checkMessageStatus(String customId)
      throws EgvpClientException {
    return invoke(
        () ->
            this.client.getForObject(
                "/getMessageDeliveryStatus/{customId}",
                MessageDeliveryStatusResponse.class,
                customId));
  }

  interface Operation<T> {
    T execute() throws RestClientException;
  }

  private <T> T invoke(Operation<T> operation) throws EgvpClientException {
    try {
      return operation.execute();
    } catch (HttpClientErrorException clientErrorException) {
      ResponseError re = clientErrorException.getResponseBodyAs(ResponseError.class);
      if (Objects.isNull(re)) {
        throw new EgvpClientException(clientErrorException.getMessage(), clientErrorException);
      }
      throw new EgvpClientException(re.responseCode(), clientErrorException);
    } catch (HttpServerErrorException serverErrorException) {
      throw new EgvpClientException(
          serverErrorException.getStatusCode().toString(), serverErrorException);
    }
  }
}
