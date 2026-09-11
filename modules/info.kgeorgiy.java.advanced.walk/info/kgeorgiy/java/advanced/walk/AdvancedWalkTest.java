package info.kgeorgiy.java.advanced.walk;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Tests for advanced version
 * of <a href="https://www.kgeorgiy.info/courses/java-advanced/homeworks.html#homework-walk">Walk</a> homework
 * for <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class AdvancedWalkTest extends RecursiveWalkTest {
    public AdvancedWalkTest() {
    }

    private static final WalkUtil other = new WalkUtil() {
        @Override
        public String hash(final byte[] bytes) {
            if (bytes == ERROR_CONTENT) {
                return errorHash;
            }
            long hash;
            if (bytes == null) {
                hash = 0;
            } else {
                hash = 0xcbf29ce484222325L;
                for (final byte b : bytes) {
                    hash = (hash * 0x00000100000001B3L) ^ (b & 0xff);
                }
            }
            return "%016x".formatted(hash);
        }

        @Override
        public void run(final String... args) {
            super.run(Stream.concat(Arrays.stream(args), Stream.of("fnv-64")).toArray(String[]::new));
        }
    };

    @Override
    protected void check(
            final Collection<String> inputs,
            final Map<String, byte[]> files,
            final String inputFile,
            final String outputFile
    ) {
        super.check(inputs, files, inputFile, outputFile);
        other.check(inputs, files, inputFile, outputFile + "~2");
    }
}
