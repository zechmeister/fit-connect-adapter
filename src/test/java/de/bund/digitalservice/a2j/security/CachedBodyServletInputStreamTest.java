package de.bund.digitalservice.a2j.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import jakarta.servlet.ReadListener;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CachedBodyServletInputStreamTest {

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

    while (inputStream.read() != -1) {
      // consuming input stream
    }

    assertTrue(inputStream.isFinished());
  }

  @Test
  void testIsReady() {
    assertTrue(inputStream.isReady());
  }

  @Test
  void testSetReadListener() {
    ReadListener readListener = mock(ReadListener.class);

    assertThrows(
        UnsupportedOperationException.class, () -> inputStream.setReadListener(readListener));
  }
}
