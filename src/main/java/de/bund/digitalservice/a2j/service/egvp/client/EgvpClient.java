package de.bund.digitalservice.a2j.service.egvp.client;

import java.util.Objects;
import org.springframework.web.client.*;

public class EgvpClient {
  private final RestTemplate client;

  public EgvpClient(RestTemplate client) {
    this.client = client;
  }

  /**
   * Get Version of connected egvp enterprise instance. Mostly used for testing purposes to check if
   * a connection can be established
   *
   * @return current Version of connected egvp instance
   * @throws EgvpClientException
   */
  public GetVersionResponse getVersion() throws EgvpClientException {
    return invoke(() -> this.client.getForEntity("/getVersion", GetVersionResponse.class))
        .getBody();
  }

  public SendMessageResponse sendMessage(SendMessageRequest request) throws EgvpClientException {
    return invoke(
        () ->
            this.client
                .postForEntity(
                    "/sendMessage/{userId}", request, SendMessageResponse.class, request.userId())
                .getBody());
  }

  public MessageDeliveryStatusResponse checkMessageStatus(String userId, String customId)
      throws EgvpClientException {
    return invoke(
        () ->
            this.client.getForObject(
                "/getMessageDeliveryStatus/{userId}/{customId}",
                MessageDeliveryStatusResponse.class,
                userId,
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
    } catch (RestClientException ex) {

      throw new EgvpClientException(ex.getMessage(), ex);
    }
  }
}
