package de.bund.digitalservice.a2j.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import de.bund.digitalservice.a2j.service.egvp.DTO.GetVersionResponse;
import de.bund.digitalservice.a2j.service.egvp.EgvpClient;
import de.bund.digitalservice.a2j.service.egvp.EgvpClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
  void getVersion() {
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
    GetVersionResponse res = null;

    try {
      res = client.getVersion();
    } catch (EgvpClientException e) {
      throw new RuntimeException(e);
    }

    assertEquals(new GetVersionResponse("6.0.1"), res);
  }
}
