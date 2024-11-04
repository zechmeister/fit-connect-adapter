package de.bund.digitalservice.a2j.security;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.springframework.util.StreamUtils;

public class CachedBodyServletRequest extends HttpServletRequestWrapper {

  private byte[] cachedBody;

  public CachedBodyServletRequest(HttpServletRequest request) throws IOException {
    super(request);
    InputStream requestInputStream = request.getInputStream();
    this.cachedBody = StreamUtils.copyToByteArray(requestInputStream);
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    return new CachedBodyServletInputStream(this.cachedBody);
  }

  @Override
  public BufferedReader getReader() throws IOException {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
    return new BufferedReader(new InputStreamReader(byteArrayInputStream));
  }
}
