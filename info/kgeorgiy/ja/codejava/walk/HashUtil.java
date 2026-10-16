package kgeorgiy.ja.codejava.walk;

import java.io.IOException;
import java.io.InputStream;

public class HashUtil {
  private HashUtil() {
  }

  private static final int FNV_32_PRIME = 0x01000193;
  private static final int FNV_32_OFFSET_BASIS = 0x811c9dc5;

  public static int FNV1Hash(InputStream is) throws IOException {
    int hash = FNV_32_OFFSET_BASIS;
    int b;

    while ((b = is.read()) != -1) {
      hash *= FNV_32_PRIME;
      hash ^= b;
    }

    return hash;
  }
}
