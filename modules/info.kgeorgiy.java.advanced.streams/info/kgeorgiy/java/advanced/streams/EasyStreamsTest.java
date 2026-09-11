package info.kgeorgiy.java.advanced.streams;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Gatherer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static net.java.quickcheck.generator.CombinedGenerators.lists;
import static net.java.quickcheck.generator.PrimitiveGenerators.strings;

/**
 * Full tests for easy version.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class EasyStreamsTest<S extends EasyStreams> extends StreamsTest<S> {
    public EasyStreamsTest() {
    }

    // === Spliterators

    @Test
    public void test01_binaryTreeSpliterator() {
        testSpliterator(
                this.<Trees.Binary<String>>plain(binary(Trees.Binary.Branch::new)),
                streams::binaryTreeSpliterator,
                checker(s -> s == 1, Characteristic.IMMUTABLE)
        );
    }

    @Test
    public void test02_sizedBinaryTreeSpliterator() {
        testSpliterator(
                this.<Trees.SizedBinary<String>>plain(binary(Trees.SizedBinary.Branch::new)),
                streams::sizedBinaryTreeSpliterator,
                checker(s -> s >= 0, Characteristic.SIZED, Characteristic.SUBSIZED, Characteristic.IMMUTABLE)
        );
    }

    @Test
    public void test03_naryTreeSpliterator() {
        testSpliterator(
                this.<Trees.Nary<String>>plain(nary(Trees.Nary.Node::new)),
                streams::naryTreeSpliterator,
                checker(s -> s == 1, Characteristic.IMMUTABLE)
        );
    }

    <T, M> Stream<Named<Consumer<TreeTest<T, M>>>> checkers(final TestSpliterator<T, M> spliterator) {
        return Stream.concat(super.checkers(spliterator), Stream.of(
                spliterator.checker(".trySplit()", tail -> {
                    if (tail.getExactSizeIfKnown() != 1) {
                        final Spliterator<T> head = tail.trySplit();
                        spliterator.checker().accept(head, -1L);
                        return Stream.of(head, tail).flatMap(StreamsTest::stream);
                    } else {
                        return stream(tail);
                    }
                }),
                spliterator.checker(".tryAdvance() [single]", split -> {
                    final List<T> result = new ArrayList<>();
                    int size = 0;
                    while (split.tryAdvance(result::add)) {
                        Assertions.assertEquals(1, result.size() - size, "accept calls");
                        size++;
                    }
                    return result.stream();
                }),
                spliterator.performance(10_000)
        ));
    }


    // === Collectors

    @Test
    public void test11_first() {
        testCollectorIndex("first()", streams.first(), size -> 0);
    }

    @Test
    public void test12_last() {
        testCollectorIndex("last()", streams.last(), size -> size - 1);
    }

    @Test
    public void test13_middle() {
        testCollectorIndex("middle()", streams.middle(), size -> size / 2);
    }

    @Test
    public void test21_commonPrefix() {
        testCommon("commonPrefix()", streams.commonPrefix(), (a, b) -> b + a, StreamsTest::commonPrefix);
    }

    @Test
    public void test22_commonSuffix() {
        testCommon("commonSuffix()", streams.commonSuffix(), String::concat, (a, b) -> reverse(commonPrefix(reverse(a), reverse(b))));
    }

    private static String reverse(final String string) {
        return new StringBuilder(string).reverse().toString();
    }


    // === Gatherers

    @Test
    public void test31_stringPrefixes() {
        testStringGatherer(
                "stringPrefixes",
                streams.stringPrefixes(),
                (value, i) -> value.substring(0, i)
        );
    }

    protected void testStringGatherer(
            final String method,
            final Gatherer<CharSequence, ?, CharSequence> gatherer,
            final BiFunction<String, Integer, String> answer
    ) {
        testGatherer(
                method,
                gatherer,
                lists(strings(30)),
                maxStreamSize() / 10,
                values -> {
                    final List<CharSequence> result = new ArrayList<>();
                    for (final String value : values) {
                        for (int i = 1; i <= value.length(); i++) {
                            result.add(answer.apply(value, i));
                        }
                    }
                    return result;
                }
        );
    }

    @Test
    public void test32_nth() {
        IntStream.rangeClosed(1, 10).forEachOrdered(n -> testGatherer(
                "nth(%d)".formatted(n),
                streams.nth(n),
                lists(strings(3)),
                maxStreamSize(),
                values -> {
                    final List<String> result = new ArrayList<>();
                    for (int i = n - 1; i < values.size(); i += n) {
                        result.add(values.get(i));
                    }
                    return result;
                }
        ));
    }

    @Test
    public void test33_distinctPrefix() {
        testDistinct("distinctPrefix", streams.distinctPrefix(), Function.identity());
    }

    protected <R> void testDistinct(
            final String name,
            final Gatherer<String, ?, String> gatherer,
            final Function<String, R> keyF
    ) {
        testGatherer(
                "distinctPrefixBy(%s)".formatted(name),
                gatherer,
                lists(strings(3)),
                maxStreamSize(),
                values -> {
                    final Set<R> visited = new HashSet<>();
                    return values.subList(0, (int) IntStream.range(0, values.size()).takeWhile(i -> visited.add(keyF.apply(values.get(i)))).count());
                }
        );
    }


    // === Settings

    @Override
    int indexOrigin() {
        return -1;
    }

    @Override
    int maxStreamSize() {
        return 1 << 17;
    }
}
