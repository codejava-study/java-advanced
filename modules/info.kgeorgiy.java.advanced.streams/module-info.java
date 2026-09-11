/**
 * Tests for <a href="https://www.kgeorgiy.info/courses/java-advanced/homeworks.html#homework-streams">Streams Implementation</a> homework
 * of <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
module info.kgeorgiy.java.advanced.streams {
    requires transitive info.kgeorgiy.java.advanced.base;
    requires quickcheck;
    requires org.junit.jupiter.api;

    exports info.kgeorgiy.java.advanced.streams;

    opens info.kgeorgiy.java.advanced.streams to org.junit.platform.launcher;
}
