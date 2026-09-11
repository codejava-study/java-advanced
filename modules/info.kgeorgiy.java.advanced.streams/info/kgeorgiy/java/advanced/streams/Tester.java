package info.kgeorgiy.java.advanced.streams;

import info.kgeorgiy.java.advanced.base.BaseTester;

/**
 * Test launcher.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public enum Tester {
    ;

    public static final BaseTester TESTER = new BaseTester()
            .add("Easy", EasyStreamsTest.class)
            .add("Hard", HardStreamsTest.class)
            .add("Advanced", AdvancedStreamsTest.class)
            ;

    public static void main(final String... args) {
        TESTER.main(args);
    }
}
