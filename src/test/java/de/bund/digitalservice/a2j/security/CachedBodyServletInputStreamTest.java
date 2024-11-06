package de.bund.digitalservice.a2j.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.servlet.ReadListener;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CachedBodyServletInputStreamTest {

  private CachedBodyServletInputStream inputStream;
  private byte[] data;

  @BeforeEach
  void setup() {
    data = "test".getBytes();
    inputStream = new CachedBodyServletInputStream(data);
  }

  @Test
  void testRead() throws IOException {
    for (byte b : data) {
      assertEquals(b, inputStream.read());
    }
    assertEquals(-1, inputStream.read());
  }

  @Test
  void testIsFinished() throws IOException {
    assertFalse(inputStream.isFinished());

    while (inputStream.read() != -1) {}

    assertTrue(inputStream.isFinished());
  }

  @Test
  void testIsReady() {
    assertTrue(inputStream.isReady());
  }

  @Test
  void testSetReadListener() {
    assertThrows(
        UnsupportedOperationException.class,
        () ->
            inputStream.setReadListener(
                new ReadListener() {
                  @Override
                  public void onDataAvailable() throws IOException {}

                  @Override
                  public void onAllDataRead() throws IOException {}

                  @Override
                  public void onError(Throwable throwable) {}
                }));
  }
}
