package de.bund.digitalservice.a2j.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.fitko.fitconnect.client.SubscriberClient;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class SecurityTxtIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @MockBean SubscriberClient subscriberClient;

  @Test
  void shouldExposeSecurityTxt() throws Exception {
    mockMvc
        .perform(get("/.well-known/security.txt"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.TEXT_PLAIN))
        .andExpect(result -> assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty());
  }
}
