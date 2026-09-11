package info.kgeorgiy.java.advanced.iterative;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Full tests for easy version.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class ScalarIPTest<P extends NewScalarIP> extends BaseIPTest<P> {
    public static final int PROCESSORS = 4; //Runtime.getRuntime().availableProcessors();
    protected final int[] STEPS = {1, 3, 5, 1000_000};

    public ScalarIPTest() {
    }

    @Test
    public void test01_argMax() {
        test((values, comparator) -> values.indexOf(Collections.max(values, comparator)), P::argMax, COMPARATORS);
    }

    @Test
    public void test02_argMin() {
        test((values, comparator) -> values.indexOf(Collections.min(values, comparator)), P::argMin, COMPARATORS);
    }

    @Test
    public void test03_indexOf() {
        testS((values, predicate) -> values.map(predicate::test).toList().indexOf(true), P::indexOf, PREDICATES);
    }

    @Test
    public void test04_lastIndexOf() {
        testS((values, predicate) -> values.map(predicate::test).toList().lastIndexOf(true), P::lastIndexOf, PREDICATES);
    }

    @Test
    public void test05_sumIndices() {
        testS(
                (stream, predicate) -> {
                    final List<Boolean> flags = stream.map(predicate::test).toList();
                    return IntStream.range(0, flags.size()).filter(flags::get).mapToLong(a -> a).sum();
                },
                P::sumIndices,
                PREDICATES
        );
        factors.stream().map(this::createInstance).forEachOrdered(instance ->
                IntStream.rangeClosed(1, MAX_THREADS).forEachOrdered(threads ->
                        IntStream.iterate(1, n -> n * 10).limit(7).forEachOrdered(n -> {
                            try {
                                Assertions.assertEquals(
                                        n * (n - 1L) / 2,
                                        instance.sumIndices(threads, Collections.nCopies(n, null), v -> true),
                                        () -> "For threads = %d, n = %d".formatted(threads, n)
                                );
                            } catch (final InterruptedException e) {
                                throw new AssertionError(e);
                            }
                        }))
        );
    }

    @Test
    public void test10_sleepPerformance() {
        testPerformance("argMax", MAX_THREADS, 3, 1, 1.5, (instance, threads, values) ->
                instance.argMax(threads, values, (o1, o2) -> sleep(Integer.compare(o1, o2))));
        testPerformance("sumIndices", MAX_THREADS, 5, 0, 1.5, (instance, threads, values) ->
                instance.sumIndices(threads, values, v -> sleep(v % 3 == 1)));
    }

    @Test
    public void test11_burnPerformance() {
        testPerformance("argMax", PROCESSORS, 50, 1, 1.5, (instance, threads, values) ->
                instance.argMax(threads, values, (o1, o2) -> burn(Integer.compare(o1, o2))));
        testPerformance("sumIndices", PROCESSORS, 50, 0, 1.5, (instance, threads, values) ->
                instance.sumIndices(threads, values, v1 -> burn(v1 % 3 == 1)));
    }

    @Test
    public void test91_stepArgMax() {
        testStep((values, comparator, step) -> index(values, Function.identity(), Collections.max(values, comparator), List::indexOf, step), P::argMax, COMPARATORS);
    }

    @Test
    public void test92_stepArgMin() {
        testStep((values, comparator, step) -> index(values, Function.identity(), Collections.min(values, comparator), List::indexOf, step), P::argMin, COMPARATORS);
    }

    @Test
    public void test93_indexOf() {
        testStep((values, predicate, step) -> index(values, predicate::test, true, List::indexOf, step), P::indexOf, PREDICATES);
    }

    @Test
    public void test94_lastIndexOf() {
        testStep((values, predicate, step) -> index(values, predicate::test, true, List::lastIndexOf, step), P::lastIndexOf, PREDICATES);
    }

    private static <T, R> int index(
            final List<T> values,
            final Function<T, R> f,
            final R value,
            final BiFunction<List<R>, R, Integer> index,
            final int step
    ) {
        return Math.max(index.apply(values.stream().map(f).toList(), value) * step, -1);
    }

    @Test
    public void test95_sumIndices() {
        testStep(
                (stream, predicate, step) -> {
                    final List<Boolean> flags = stream.stream().map(predicate::test).toList();
                    return IntStream.range(0, flags.size()).filter(flags::get).mapToLong(a -> a).sum() * step;
                },
                P::sumIndices,
                PREDICATES
        );
        factors.stream().map(this::createInstance).forEachOrdered(instance ->
                IntStream.rangeClosed(1, MAX_THREADS).forEachOrdered(threads ->
                        IntStream.iterate(1, n -> n * 10).limit(7).forEachOrdered(n ->
                                Arrays.stream(STEPS).forEachOrdered(step -> {
                                    try {
                                        final int nn = (n + step - 1) / step;
                                        Assertions.assertEquals(
                                                nn * (nn - 1L) / 2 * step,
                                                instance.sumIndices(threads, Collections.nCopies(n, null), value -> true, step),
                                                () -> "For threads = %d, n = %d, step = %d".formatted(threads, n, step)
                                        );
                                    } catch (final InterruptedException e) {
                                        throw new AssertionError(e);
                                    }
                                }))));
    }

    @Test
    public void test96_stepPerformance() throws InterruptedException {
        this.<Comparator<Integer>>testStepPerformance(P::argMax, Integer::compare, "argMax");
        this.<Predicate<Integer>>testStepPerformance(P::sumIndices, a -> a > 0, "sumIndices");
    }

    private <U> void testStepPerformance(
            final StepFunction<P, U, ?> f,
            final U value,
            final String name
    ) throws InterruptedException {
        context.context(name, () -> {
            final List<Integer> data = randomList(1_000);
            final long denseTime = measureTime(data, 2, f, value);
            final long sparseTime = measureTime(data, 2000_000, f, value);

            final double ratio = sparseTime / (double) denseTime;
            context.println("%s, performance ratio %.3f (%.1f %.1f)".formatted(name, ratio, sparseTime / 1e9, denseTime / 1e9));
            checkRatio(ratio, 3);
        });
    }

    private <U> long measureTime(
            final List<Integer> data,
            final int step,
            final StepFunction<P, U, ?> f,
            final U value
    ) throws InterruptedException {
        return measureTime(
                1,
                sparse(data, step),
                (instance, threads, values) -> {
                    for (int i = 0; i < 3_000; i++) {
                        f.apply(instance, threads, values, value, step);
                    }
                },
                1
        );
    }

    protected final <T, U> void testStep(
            final StepAnswer<List<Integer>, U, T> fExpected,
            final StepFunction<P, U, T> fActual,
            final List<Named<U>> cases
    ) {
        for (final int step : STEPS) {
            try {
                test(
                        (data, value) -> fExpected.apply(nth(data, step), value, step),
                        (instance, threads, data, value) -> fActual.apply(instance, threads, data, value, step),
                        cases.stream().map(named -> named.extend("Step " + step)).toList()
                );
            } catch (final AssertionError e) {
                throw new AssertionError("Step " + step + ": " + e.getMessage(), e);
            }
        }
    }

    protected static <T> List<T> nth(final List<T> items, final int step) {
        return IntStream.iterate(0, i -> i < items.size(), i -> i + step).mapToObj(items::get).toList();
    }

    public interface StepFunction<P, U, T> {
        T apply(P instance, int threads, List<Integer> data, U value, int step) throws InterruptedException;
    }

    public interface StepAnswer<P, U, T> {
        T apply(P values, U value, int step);
    }

    private static <E> List<E> sparse(final List<E> items, final int step) {
        return new AbstractList<>() {
            @Override
            public E get(final int index) {
                return items.get(index / step);
            }

            @Override
            public int size() {
                return items.size() * step;
            }
        };
    }

    protected void testPerformance(
            final String name,
            final int threads,
            final int sizeMultiplier,
            final int sequentialWeight,
            final double delta,
            final ConcurrentConsumer<P, Integer, List<Integer>> consumer
    ) {
        new PerformanceTest(name, threads, sequentialWeight, consumer).test(sizeMultiplier * threads, delta);
    }

    protected static <T> T sleep(final T result) {
        try {
            Thread.sleep(50);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return result;
    }

    protected static <T> T burn(final T result) {
        int total = result.hashCode();
        for (int i = 0; i < 10_000_000; i++) {
            total += i;
        }
        if (total == result.hashCode()) {
            throw new AssertionError("No burn");
        }
        return result;
    }


    protected class PerformanceTest {
        private static final int WARMUP_CYCLES = 3;

        private final String name;
        private final int threads;
        private final int sequentialWeight;
        private final ConcurrentConsumer<P, Integer, List<Integer>> consumer;

        protected PerformanceTest(
                final String name,
                final int threads,
                final int sequentialWeight,
                final ConcurrentConsumer<P, Integer, List<Integer>> consumer
        ) {
            this.name = name;
            this.threads = threads;
            this.sequentialWeight = sequentialWeight;
            this.consumer = consumer;
        }

        private double measure(final int realThreads, final List<Integer> data, final String name) {
            return context.context(name, () -> {
                final int subtasks = getSubtasks(realThreads, threads);
                assert subtasks % realThreads == 0 && data.size() % subtasks == 0;

                final long time = measureTime(realThreads, data, consumer, subtasks);

                final int sequential = (subtasks - 1) * sequentialWeight;
                final int parallel = (data.size() - subtasks) / realThreads;
                return time / 1e6 / (sequential + parallel);
            });
        }

        private double speedup(final int size) {
            return context.context(name, () -> {
                final int subtasks = getSubtasks(threads, threads);
                final List<Integer> data = List.copyOf(randomList(((size - 1) / subtasks + 1) * subtasks));

                for (int i = 0; i < WARMUP_CYCLES; i++) {
                    measure(threads, data, "Warm up (%d/%d)".formatted(i + 1, WARMUP_CYCLES));
                }

                final double performance1 = measure(1, data, "Measure single-threaded");
                final double performance2 = measure(threads, data, "Measure multi-threaded");
                final double speedup = performance2 / performance1;
                context.println("Performance ratio %.1f for %d threads (%.1f %.1f ms/op)"
                        .formatted(speedup, threads, performance1, performance2));
                return speedup;
            });
        }

        protected void test(final int size, final double delta) {
            checkRatio(speedup(size), delta);
        }
    }

    protected static void checkRatio(final double value, final double delta) {
        Assertions.assertTrue(value > 1 / delta, "Lower bound hit: %.1f".formatted(value));
        Assertions.assertTrue(value < delta, "Upper bound hit: %.1f".formatted(value));
    }

    protected long measureTime(
            final int threads,
            final List<Integer> data,
            final ConcurrentConsumer<P, Integer, List<Integer>> consumer,
            final int subtasks
    ) throws InterruptedException {
        final P instance = createInstance(threads);
        final long start = System.nanoTime();
        consumer.accept(instance, subtasks, data);
        return System.nanoTime() - start;
    }
}
