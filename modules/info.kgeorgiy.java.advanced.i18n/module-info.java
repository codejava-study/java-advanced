/**
 * Tests for <a href="https://www.kgeorgiy.info/courses/java-advanced/homeworks.html#homework-text-stats">Text Statistics</a> homework
 * of <a href="https://www.kgeorgiy.info/courses/java-advanced/">Java Advanced</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
module info.kgeorgiy.java.advanced.i18n {
    requires transitive info.kgeorgiy.java.advanced.base;
    requires quickcheck;

    exports info.kgeorgiy.java.advanced.i18n;

    opens info.kgeorgiy.java.advanced.i18n to org.junit.platform.launcher;
}
