package de.bund.digitalservice.a2j.service.subscriber;

import dev.fitko.fitconnect.api.domain.validation.ValidationResult;
import dev.fitko.fitconnect.client.SenderClient;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Component
public class CallbackVerificationFilter extends OncePerRequestFilter {
  private final SenderClient senderClient;
  private final String callbackSecret;
  private final Logger logger = LoggerFactory.getLogger(CallbackVerificationFilter.class);

  public CallbackVerificationFilter(
      SenderClient senderClient, @Value("${fitConnect.callbackSecret}") String callbackSecret) {
    this.senderClient = senderClient;
    this.callbackSecret = callbackSecret;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !request.getServletPath().startsWith("/callbacks/fit-connect");
  }

  @Override
  protected void doFilterInternal(
      @NotNull HttpServletRequest request,
      @NotNull HttpServletResponse response,
      @NotNull FilterChain chain)
      throws ServletException, IOException {

    ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
    String requestBody =
        new String(wrappedRequest.getContentAsByteArray(), request.getCharacterEncoding());

    ValidationResult result = senderClient
        .validateCallback(
            request.getHeader("callback-authentication"),
            Long.parseLong(request.getHeader("callback-timestamp")),
            requestBody,
            callbackSecret);

    if (!result.isValid()) {
      logger.info("Received invalid fit-connect callback");
      logger.info(result.getProblems().toString());
      logger.info(result.getError().getMessage());
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    chain.doFilter(request, response);
  }
}
