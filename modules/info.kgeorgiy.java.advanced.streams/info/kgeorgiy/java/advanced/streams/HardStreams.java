package info.kgeorgiy.java.advanced.streams;

import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Gatherer;

/**
 * Hard-version interface.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface HardStreams extends EasyStreams {
    /**
     * Returns binary tree of lists spliterator.
     * This method runs in O(1) time.
     */
    <T> Spliterator<T> nestedBinaryTreeSpliterator(Trees.Binary<List<T>> tree);

    /**
     * Returns sized binary tree of lists spliterator.
     * This method runs in O(1) time.
     */
    <T> Spliterator<T> nestedSizedBinaryTreeSpliterator(Trees.SizedBinary<List<T>> tree);

    /**
     * Returns nary tree of lists spliterator.
     * This method runs in O({@code n}) time, where {@code n} is the size of the root.
     */
    <T> Spliterator<T> nestedNaryTreeSpliterator(Trees.Nary<List<T>> tree);

    /**
     * Returns at most {@code k} first elements.
     * This collector runs in O({@code k}) memory.
     */
    <T> Collector<T, ?, List<T>> head(int k);

    /**
     * Returns at most {@code k} last elements.
     * This collector runs in O({@code k}) memory.
     */
    <T> Collector<T, ?, List<T>> tail(int k);

    /**
     * Returns {@code k}-th element of the stream, if present.
     * This collector runs in O(1) memory.
     */
    <T> Collector<T, ?, Optional<T>> kth(final int k);

    /**
     * Gathers a stream of non-empty suffixes of given strings.
     * For each string emits suffixes in order from the shortest to the longest.
     * This gatherer runs in O({@code n}) memory, where {@code n} is the length of longest string.
     */
    Gatherer<CharSequence, ?, CharSequence> stringSuffixes();

    /**
     * Returns {@code i}-th element of each non-overlapping {@code n}-element window.
     * This gatherer runs in O(1) memory.
     */
    <T> Gatherer<T, ?, T> ithOfN(int i, int n);

    /**
     * Returns prefix up to first duplicate value produced by a key function (not included).
     * This gatherer runs in O({@code n}) memory, where {@code n} is the length of the prefix returned.
     */
    <T, K> Gatherer<T, ?, T> distinctPrefixBy(final Function<? super T, K> keyF);
}
