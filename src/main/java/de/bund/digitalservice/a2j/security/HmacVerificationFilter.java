package de.bund.digitalservice.a2j.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.filter.OncePerRequestFilter;

public class HmacVerificationFilter extends OncePerRequestFilter {
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !request.getServletPath().startsWith("/callbacks/fit-connect");
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    System.out.println("Received callback, returned 401");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }
}
