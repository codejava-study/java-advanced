package kgeorgiy.ja.codejava.walk;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import static kgeorgiy.ja.codejava.walk.WalkUtil.printUsageTip;
import static kgeorgiy.ja.codejava.walk.HashUtil.FNV1Hash;

public class Walk {

  public static void main(String[] args) {
    if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
      printUsageTip(Walk.class);
      return;
    }

    try {
      Path in = Path.of(args[0]);
      Path out = Path.of(args[1]);

      checkOutParentDirs(out);

      try (
          BufferedReader lineReader = new BufferedReader(
              new InputStreamReader(new FileInputStream(in.toFile()), StandardCharsets.UTF_8));
          Writer writer = new BufferedWriter(
              new OutputStreamWriter(new FileOutputStream(out.toFile()), StandardCharsets.UTF_8))) {
        String line;
        while ((line = lineReader.readLine()) != null) {
          processFile(line, writer);
        }
      }
    } catch (IOException | InvalidPathException e) {
      System.err.println("I/O error: " + e.getMessage());
    }
  }

  private static void processFile(String filename, Writer writer) throws IOException {
    int hash;

    try (InputStream is = new BufferedInputStream(
        Files.newInputStream(Path.of(filename)))) {
      hash = FNV1Hash(is);
    } catch (IOException | InvalidPathException e) {
      hash = 0;
    }

    writer.write(String.format("%08x", hash) + " " + filename
        + System.lineSeparator());
  }

  private static void checkOutParentDirs(Path out) throws IOException {
    Path parent = out.toAbsolutePath().getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }
  }
}
