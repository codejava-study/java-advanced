package info.kgeorgiy.java.advanced.streams;

import info.kgeorgiy.java.advanced.streams.Trees.Binary;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import static net.java.quickcheck.generator.CombinedGenerators.lists;
import static net.java.quickcheck.generator.PrimitiveGenerators.strings;

/**
 * Full tests for hard version.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class HardStreamsTest<S extends HardStreams> extends EasyStreamsTest<S> {
    public HardStreamsTest() {
    }


    // === Spliterators

    @Test
    public void test51_nestedBinaryTreeSpliterator() {
        testSpliterator(
                this.<Binary<List<String>>>nested(binary(Binary.Branch::new)),
                streams::nestedBinaryTreeSpliterator,
                checker(s -> false)
        );
    }

    @Test
    public void test52_nestedSizedBinaryTreeSpliterator() {
        testSpliterator(
                this.<Trees.SizedBinary<List<String>>>nested(binary(Trees.SizedBinary.Branch::new)),
                streams::nestedSizedBinaryTreeSpliterator,
                checker(s -> false)
        );
    }

    @Test
    public void test53_nestedNaryTreeSpliterator() {
        testSpliterator(
                this.<Trees.Nary<List<String>>>nested(nary(Trees.Nary.Node::new)),
                streams::nestedNaryTreeSpliterator,
                checker(s -> false)
        );
    }


    // === Collectors

    @Test
    public void test61_head() {
        testSized("head(%d)", streams::head, (values, k) -> values.subList(0, k));
    }

    @Test
    public void test62_tail() {
        testSized("tail(%d)", streams::tail, (values, k) -> values.subList(values.size() - k, values.size()));
    }

    @Test
    public void test63_kth() {
        testSizedCollector("kth(%d)", streams::kth, StreamsTest::get);
    }


    // === Gatherers

    @Test
    public void test71_stringSuffixes() {
        testStringGatherer(
                "stringSuffixes",
                streams.stringSuffixes(),
                (value, i) -> value.substring(value.length() - i)
        );
    }

    @Test
    public void test72_ithOfN() {
        IntStream.rangeClosed(1, 5).forEachOrdered(n ->
                IntStream.range(0, n - 1).forEachOrdered(i -> testGatherer(
                                "ithOfN(%d, %d)".formatted(i, n),
                                streams.ithOfN(i, n),
                                lists(strings(3)),
                                maxStreamSize() / 2,
                                values -> {
                                    final List<String> result = new ArrayList<>();
                                    for (int j = i; j < values.size(); j += n) {
                                        result.add(values.get(j));
                                    }
                                    return result;
                                }
                        )
                ));
    }

    private static final List<Named<Function<String, ?>>> DISTINCT_BY = List.of(
            named("identity", Function.identity()),
            named("toLower", String::toLowerCase),
            named("length", String::length),
            named("identityHashCode", System::identityHashCode)
    );

    @Test
    public void test73_distinctPrefixBy() {
        for (final Named<Function<String, ?>> keyF : DISTINCT_BY) {
            testDistinct("distinctPrefixBy(%s)".formatted(keyF.name()), streams.distinctPrefixBy(keyF.value()), keyF.value());
        }
    }
}
