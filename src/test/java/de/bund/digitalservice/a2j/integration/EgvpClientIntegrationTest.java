package de.bund.digitalservice.a2j.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.a2j.service.egvp.client.EgvpClient;
import de.bund.digitalservice.a2j.service.egvp.client.EgvpClientException;
import de.bund.digitalservice.a2j.service.egvp.client.MessageDeliveryStatusResponse;
import de.bund.digitalservice.a2j.service.egvp.client.SendMessageRequest;
import de.bund.digitalservice.a2j.service.egvp.client.SendMessageResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

// only extend with SpringExtension to avoid loading the whole Application context
@ExtendWith(SpringExtension.class)
@Tag("integration")
class EgvpClientIntegrationTest {

  private EgvpClient client;

  private MockRestServiceServer mockServer;

  @BeforeEach
  void setup() {
    RestTemplate restTemplate = new RestTemplateBuilder().rootUri("http://localhost:8088").build();
    this.client = new EgvpClient(restTemplate);
    this.mockServer = MockRestServiceServer.createServer(restTemplate);
  }

  @Test
  void checkMessageStatus() throws EgvpClientException {
    mockServer
        .expect(requestTo("http://localhost:8088/getMessageDeliveryStatus/userId/12345"))
        .andRespond(
            withSuccess(
                """
                                    {
                                      "messageId":"MSG_ID_123",
                                      "delivered":true,
                                      "pendingReason":"",
                                      "path": "path_to_file.pdf"
                                    }
                                    """,
                MediaType.APPLICATION_JSON));
    MessageDeliveryStatusResponse expectedResponse =
        new MessageDeliveryStatusResponse("MSG_ID_123", true, "", "path_to_file.pdf");

    assertEquals(expectedResponse, client.checkMessageStatus("userId", "12345"));
  }

  @Test
  void sendMessage() throws JsonProcessingException, EgvpClientException {
    SendMessageRequest request =
        new SendMessageRequest(
            "userId", "receiverId", "mailbox", "subject", "attachmentFilepath", "xjustizFilepath");

    mockServer
        .expect(requestTo("http://localhost:8088/sendMessage/userId"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(content().json(new ObjectMapper().writeValueAsString(request)))
        .andRespond(
            withSuccess()
                .body(
                    """
                                                      {
                                                          "customId":"ABCDE12345"
                                                      }
                                                      """)
                .contentType(MediaType.APPLICATION_JSON));

    assertEquals(new SendMessageResponse("ABCDE12345"), client.sendMessage(request));
  }

  @Test
  void sendMessageClientFailure() throws JsonProcessingException {
    SendMessageRequest request =
        new SendMessageRequest(
            "userId", "receiverId", "mailbox", "subject", "attachmentFilepath", "xjustizFilepath");

    mockServer
        .expect(requestTo("http://localhost:8088/sendMessage/userId"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(content().json(new ObjectMapper().writeValueAsString(request)))
        .andRespond(
            withBadRequest()
                .body(
                    """
                                      {
                                          "responseCode":"ZERO_SIZE_ATTACHMENT"
                                      }
                                      """)
                .contentType(MediaType.APPLICATION_JSON));
    EgvpClientException ex =
        Assertions.assertThrows(EgvpClientException.class, () -> client.sendMessage(request));

    assertEquals("ZERO_SIZE_ATTACHMENT", ex.getMessage());
  }

  @Test
  void sendMessageServerFailure() throws JsonProcessingException {
    SendMessageRequest request =
        new SendMessageRequest(
            "userId", "receiverId", "mailbox", "subject", "attachmentFilepath", "xjustizFilepath");

    mockServer
        .expect(requestTo("http://localhost:8088/sendMessage/userId"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(content().json(new ObjectMapper().writeValueAsString(request)))
        .andRespond(withServerError());
    EgvpClientException ex =
        Assertions.assertThrows(EgvpClientException.class, () -> client.sendMessage(request));

    assertEquals("500 INTERNAL_SERVER_ERROR", ex.getMessage());
  }
}
