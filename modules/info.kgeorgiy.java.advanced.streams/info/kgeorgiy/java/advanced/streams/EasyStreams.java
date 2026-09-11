package info.kgeorgiy.java.advanced.streams;

import java.util.Optional;
import java.util.Spliterator;
import java.util.stream.Collector;
import java.util.stream.Gatherer;

/**
 * Easy-version interface.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface EasyStreams {
    /**
     * Returns binary tree spliterator.
     * This method runs in O(1) time.
     */
    <T> Spliterator<T> binaryTreeSpliterator(Trees.Binary<T> tree);

    /**
     * Returns sized binary tree spliterator.
     * This method runs in O(1) time.
     */
    <T> Spliterator<T> sizedBinaryTreeSpliterator(Trees.SizedBinary<T> tree);

    /**
     * Returns nary tree spliterator.
     * This method runs in O({@code n}) time, where {@code n} is the size of the root.
     */
    <T> Spliterator<T> naryTreeSpliterator(Trees.Nary<T> tree);

    /**
     * Returns the first element of the stream, if present.
     * This collector runs in O(1) memory.
     */
    <T> Collector<T, ?, Optional<T>> first();

    /**
     * Returns the last element of the stream, if present.
     * This collector runs in O(1) memory.
     */
    <T> Collector<T, ?, Optional<T>> last();

    /**
     * Returns middle element of the stream, if present.
     * This collector runs in O({@code n}) memory, where {@code n} is the index of the returned element.
     */
    <T> Collector<T, ?, Optional<T>> middle();

    /**
     * Computes a common prefix of input {@link CharSequence}s.
     * This collector runs in O({@code n}) memory, where {@code n} is the length of the longest sequence.
     */
    Collector<CharSequence, ?, String> commonPrefix();

    /**
     * Computes a common suffix of input {@link CharSequence}s.
     * This collector runs in O({@code n}) memory, where {@code n} is the length of the longest sequence.
     */
    Collector<CharSequence, ?, String> commonSuffix();

    /**
     * Gathers a stream of non-empty prefixes of given strings.
     * For each string emits prefixes in order from the shortest to the longest.
     * This gatherer runs in O({@code n}) memory, where {@code n} is the length of longest string.
     */
    Gatherer<CharSequence, ?, CharSequence> stringPrefixes();

    /**
     * Gathers each {@code n}-th element of the stream.
     * This gatherer runs in O(1) memory.
     */
    <T> Gatherer<T, ?, T> nth(int n);

    /**
     * Gathers a stream prefix up to the first duplicate element (not included).
     * This gatherer runs in O({@code n}) memory, where {@code n} is the length of the prefix returned.
     */
    <T> Gatherer<T, ?, T> distinctPrefix();
}
