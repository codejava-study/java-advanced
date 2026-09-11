package info.kgeorgiy.java.advanced.streams;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;

/**
 * Advanced-version interface.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface AdvancedStreams extends HardStreams {
    /**
     * Computes the {@link List} of the  input elements for which given mapper function returns distinct results.
     * This collector runs in O({@code n}) memory, where {@code n} is the size of the result list.
     */
    <T> Collector<T, ?, List<T>> distinctBy(Function<? super T, ?> mapper);

    /**
     * Finds all elements which are equal to each other and smaller than any other element according to the
     * specified {@link Comparator}.
     * This collector runs in O({@code n}) memory, where {@code n} is the size of the result.
     */
    <T> Collector<T, ?, List<T>> minimums(Comparator<? super T> comparator);

    /**
     * Finds all elements which are equal to each other and greater than any other element according to the
     * specified {@link Comparator}.
     * This collector runs in O({@code n}) memory, where {@code n} is the size of the result.
     */
    <T> Collector<T, ?, List<T>> maximums(Comparator<? super T> comparator);
}
