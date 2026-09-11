package info.kgeorgiy.java.advanced.streams;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;

import static net.java.quickcheck.generator.CombinedGenerators.lists;
import static net.java.quickcheck.generator.PrimitiveGenerators.strings;

/**
 * Tests for advanced version.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class AdvancedStreamsTest extends HardStreamsTest<AdvancedStreams> {
    private static final List<StreamsTest.Named<Comparator<String>>> COMPARATORS = List.of(
            named("naturalOrder", Comparator.naturalOrder()),
            named("String.CASE_INSENSITIVE_ORDER", String.CASE_INSENSITIVE_ORDER),
            named("String::length", Comparator.comparingInt(String::length))
    );

    public AdvancedStreamsTest() {
    }

    @Test
    public void test71_distinctBy() {
        testCollector(
                "distinctBy()",
                streams.distinctBy(String::length),
                lists(strings(10)),
                maxStreamSize(),
                strings -> {
                    final LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
                    strings.forEach(string -> map.putIfAbsent(string.length(), string));
                    return List.copyOf(map.values());
                }
        );
    }

    @Test
    public void test72_minimums() {
        testExtremes("minimums(%s)", streams::minimums, Collections::min);
    }

    @Test
    public void test73_maximums() {
        testExtremes("maximums(%s)", streams::maximums, Collections::max);
    }

    private void testExtremes(
            final String method,
            final Function<Comparator<String>, Collector<String, ?, List<String>>> collector,
            final BiFunction<List<String>, Comparator<String>, String> answer
    ) {
        testCollectorWith(method, collector, COMPARATORS, (strings, comparator) -> {
            if (strings.isEmpty()) {
                return strings;
            } else {
                final String result = answer.apply(strings, comparator);
                return strings.stream().filter(value -> comparator.compare(value, result) == 0).toList();
            }
        });
    }
}
