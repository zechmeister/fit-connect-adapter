package de.bund.digitalservice.a2j.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.a2j.service.egvp.DTO.GetVersionResponse;
import de.bund.digitalservice.a2j.service.egvp.DTO.MessageDeliveryStatusResponse;
import de.bund.digitalservice.a2j.service.egvp.DTO.SendMessageRequest;
import de.bund.digitalservice.a2j.service.egvp.EgvpClient;
import de.bund.digitalservice.a2j.service.egvp.EgvpClientException;
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
public class EgvpClientIntegrationTest {

  private EgvpClient client;

  private MockRestServiceServer mockServer;

  @BeforeEach
  void setup() {
    RestTemplate restTemplate = new RestTemplateBuilder().rootUri("http://localhost:8088").build();
    this.client = new EgvpClient(restTemplate);
    this.mockServer = MockRestServiceServer.createServer(restTemplate);
  }

  @Test
  void getVersionsuccess() {
    mockServer
        .expect(requestTo("http://localhost:8088/getVersion"))
        .andRespond(
            withSuccess(
                """
                        {
                            "version":"6.0.1"
                        }
                        """,
                MediaType.APPLICATION_JSON));

    assertEquals(new GetVersionResponse("6.0.1"), client.getVersion());
  }

  @Test
  void checkMessageStatus() {
    mockServer
        .expect(requestTo("http://localhost:8088/getMessageDeliveryStatus/12345"))
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

    assertEquals(expectedResponse, client.checkMessageStatus("12345"));
  }

  @Test
  void sendMessageFailure() throws JsonProcessingException {
    SendMessageRequest request =
        new SendMessageRequest(
            "receiverId", "mailbox", "subject", "attachmentFilepath", "xjustizFilepath");

    mockServer
        .expect(requestTo("http://localhost:8088/sendMessage"))
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
}
