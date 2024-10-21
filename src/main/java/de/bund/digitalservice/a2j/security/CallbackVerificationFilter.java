package de.bund.digitalservice.a2j.security;

import de.bund.digitalservice.a2j.service.receiver.CallbackVerificationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.filter.OncePerRequestFilter;

public class CallbackVerificationFilter extends OncePerRequestFilter {

  private final CallbackVerificationService service;

  public CallbackVerificationFilter(CallbackVerificationService service) {
    this.service = service;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !request.getServletPath().startsWith("/callbacks/fit-connect");
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    if (!service.isValidCallback(
        request.getHeader("callback-authentication"),
        request.getHeader("callback-timestamp"),
        request.getReader().lines().reduce("", String::concat))) {

      System.out.println("Received callback, returned 401");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    chain.doFilter(request, response);
  }
}
