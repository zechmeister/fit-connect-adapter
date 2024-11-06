package de.bund.digitalservice.a2j.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.fitko.fitconnect.api.domain.validation.ValidationResult;
import dev.fitko.fitconnect.client.SenderClient;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class CallbackVerificationFilterTest {

  private CallbackVerificationFilter filter;
  private MockHttpServletRequest request;

  @MockBean private SenderClient client;
  @Mock private FilterChain filterChain;

  @BeforeEach
  void setup() {
    filter = new CallbackVerificationFilter(client, "s3cr3t");
    request = new MockHttpServletRequest();
  }

  @Test
  void testShouldNotFilterForNonCallbackPath() {
    request.setServletPath("/some/path");

    assertTrue(filter.shouldNotFilter(request));
  }

  @Test
  void testShouldNotFilterForCallbackPath() {
    request.setServletPath("/callbacks/fit-connect");

    assertFalse(filter.shouldNotFilter(request));
  }

  @Test
  void testDoFilterInternalWithInvalidCallback() throws ServletException, IOException {
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.addHeader("callback-authentication", "invalidHmac");
    request.addHeader("callback-timestamp", "123456789");
    request.setContent("body".getBytes());

    when(client.validateCallback("invalidHmac", 123456789L, "body", "s3cr3t"))
        .thenReturn(ValidationResult.error("invalid"));

    filter.doFilterInternal(request, response, filterChain);
    verify(filterChain, never()).doFilter(any(), any());
  }

  @Test
  void testDoFilterInternalWithValidCallback() throws ServletException, IOException {
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.addHeader("callback-authentication", "validHmac");
    request.addHeader("callback-timestamp", "123456789");
    request.setContent("body".getBytes());

    when(client.validateCallback("validHmac", 123456789L, "body", "s3cr3t"))
        .thenReturn(ValidationResult.ok());

    filter.doFilterInternal(request, response, filterChain);
    verify(filterChain).doFilter(any(), any());
  }
}
