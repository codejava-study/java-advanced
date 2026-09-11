package info.kgeorgiy.java.advanced.iterative;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Full tests for easy version.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class ListIPTest<P extends NewListIP> extends ScalarIPTest<P> {
    public ListIPTest() {
    }

    @Test
    public void test10_sleepPerformance() {
        super.test10_sleepPerformance();
        testPerformance("filter", MAX_THREADS, 5, 0, 1.5, (instance, threads, values) ->
                instance.filter(threads, values, v -> sleep(v % 3 == 1), 1));
        testPerformance("map", MAX_THREADS, 5, 0, 1.5, (instance, threads, values) ->
                instance.map(threads, values, v -> sleep(v + 10), 1));
    }

    @Test
    public void test12_sleepPerformance() {
        testPerformance("filter", MAX_THREADS, 5, 0, 1.5, (instance, threads, values) ->
                instance.filter(threads, values, v -> sleep(v % 3 == 1)));
        testPerformance("map", MAX_THREADS, 5, 0, 1.5, (instance, threads, values) ->
                instance.map(threads, values, v -> sleep(v + 10)));
    }

    @Test
    public void test51_indices() {
        testStep(
                (values, predicate, step) -> {
                    final List<Boolean> flags = values.stream().map(predicate::test).toList();
                    return IntStream.range(0, flags.size()).filter(flags::get).map(i -> i * step).boxed().toList();
                },
                (p, threads, values, predicate, step) -> Arrays.stream(p.indices(threads, values, predicate, step))
                        .boxed()
                        .toList(),
                PREDICATES
        );
    }

    @Test
    public void test52_filter() {
        testStep((data, predicate, step) -> data.stream().filter(predicate).toList(), P::filter, PREDICATES);
    }

    @Test
    public void test53_map() {
        testStep((data, f, step) -> data.stream().map(f).toList(), P::map, FUNCTIONS);
    }

    @Test
    public void test54_mapMaximum() {
        testStep(
                (data, f, step) -> {
                    final List<String> strings = data.stream().map(f).map(Objects::toString).toList();
                    return strings.indexOf(Collections.max(strings, Comparator.naturalOrder())) * step;
                },
                (instance, threads, data, f, step) -> {
                    final List<String> mapped = instance.map(threads, data, f.andThen(Objects::toString));
                    return instance.argMax(threads, mapped, Comparator.naturalOrder(), step);
                },
                FUNCTIONS
        );
    }

    @Test
    public void test61_null() {
        testException(
                P::map,
                "NPE",
                (Function<Integer, String>) null,
                NullPointerException.class,
                ""
        );
    }

    @Test
    public void test62_rethrowNPE() {
        testException(
                P::map,
                "map",
                (Function<Integer, String>) value -> { throw new NullPointerException("Hello"); },
                NullPointerException.class,
                "Hello"
        );
        testException(
                P::filter,
                "filter",
                (Predicate<Integer>) value -> { throw new NullPointerException("Hello"); },
                NullPointerException.class,
                "Hello"
        );
    }
}
