package kgeorgiy.ja.codejava.walk;

public class WalkUtil {
  private WalkUtil() {
  }

  public static void printUsageTip(Class<?> c) {
    System.out.println("Usage: java " + c.getCanonicalName() + " <input file> <output file>");
  }
}
