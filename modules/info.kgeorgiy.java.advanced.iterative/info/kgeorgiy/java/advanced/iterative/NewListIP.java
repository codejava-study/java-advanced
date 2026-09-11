package info.kgeorgiy.java.advanced.iterative;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * List iterative parallelism support with sparse data.
 * <p>
 * Each method of this interface takes an extra positive integer {@code step}.
 * Only each {@code step}-th element of the input list should be used (counting from 0).
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface NewListIP extends NewScalarIP, ListIP {
    /**
     * Returns indices of the values matching predicate.
     *
     * @param threads number of concurrent threads.
     * @param values values to join.
     * @param predicate filter predicate.
     * @param step step size.
     * @param <T> value type.
     *
     * @return indices of the values matching predicate in increasing order.
     * @throws InterruptedException if executing thread was interrupted.
     */
    <T> int[] indices(
            int threads,
            List<? extends T> values,
            Predicate<? super T> predicate,
            int step
    ) throws InterruptedException;

    /**
     * Filters values by predicate.
     *
     * @param threads number of concurrent threads.
     * @param values values to filter.
     * @param predicate filter predicate.
     * @param step step size.
     * @param <T> value type.
     *
     * @return list of values satisfying given predicate. Order of values is preserved.
     * @throws InterruptedException if executing thread was interrupted.
     */
    <T> List<T> filter(
            int threads,
            List<? extends T> values,
            Predicate<? super T> predicate,
            int step
    ) throws InterruptedException;

    /**
     * Maps values.
     *
     * @param threads number of concurrent threads.
     * @param values values to map.
     * @param f mapper function.
     * @param step step size.
     * @param <T> value type.
     * @param <R> result type.
     *
     * @return list of values mapped by given function.
     * @throws InterruptedException if executing thread was interrupted.
     */
    <T, R> List<R> map(
            int threads,
            List<? extends T> values,
            Function<? super T, ? extends R> f,
            int step
    ) throws InterruptedException;
}
