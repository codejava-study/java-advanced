package info.kgeorgiy.java.advanced.iterative;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Full tests for easy version.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class AdvancedIPTest extends ListIPTest<AdvancedIP> {
    private static final List<Named<Monoid<Integer>>> MONOIDS = List.of(
            monoid("add", 0, Integer::sum),
            monoid("mul", 1, (a, b) -> a * b),
            monoid("min", Integer.MAX_VALUE, Integer::min)
    );

    public AdvancedIPTest() {
    }

    @Test
    public void test71_reduce() {
        testStep(
                (data, m, step) -> m.apply(data.stream()),
                (p, t, d, m, s) -> p.reduce(t, d, m.identity(), m.operator(), s),
                MONOIDS
        );
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    private <T> void testMR(final Named<MappedMonoid<Integer, T>>... cases) {
        testStep(
                (data, m, step) -> m.apply(data.stream()),
                (i, t, d, m, s) -> i.mapReduce(t, d, m.lift(), m.identity(), m.operator(), s),
                List.of(cases)
        );
    }

    @Test
    public void test72_mapReduceInteger() {
        testMR(
                mappedMonoid("*2-add", a -> a * 2, 0, Integer::sum),
                mappedMonoid("+2-mul", a -> a + 2, 1, (a, b) -> a * b)
        );
    }

    @Test
    public void test73_mapReduceString() {
        testMR(
                mappedMonoid("toString-min", Object::toString, "9".repeat(20), BinaryOperator.minBy(String::compareTo)),
                mappedMonoid("toString-concat", Object::toString, "", String::concat)
        );
    }

    private static <T> T reduce(final Stream<T> data, final T identity, final BinaryOperator<T> operator) {
        return data.reduce(identity, operator);
    }

    private static <T> Named<Monoid<T>> monoid(final String name, final T identity, final BinaryOperator<T> operator) {
        return named(name, new Monoid<>(identity, operator));
    }

    private static <T, R> Named<MappedMonoid<T, R>> mappedMonoid(
            final String name,
            final Function<T, R> lift,
            final R identity,
            final BinaryOperator<R> operator
    ) {
        return named(name, new MappedMonoid<>(lift, identity, operator));
    }

    private record Monoid<T>(T identity, BinaryOperator<T> operator) implements Function<Stream<T>, T> {
        @Override
        public T apply(final Stream<T> data) {
            return reduce(data, identity, operator);
        }
    }

    private record MappedMonoid<T, R>(Function<T, R> lift, R identity, BinaryOperator<R> operator) implements Function<Stream<T>, R> {
        @Override
        public R apply(final Stream<T> data) {
            return reduce(data.map(lift), identity, operator);
        }
    }
}
