package info.kgeorgiy.java.advanced.crawler;

import org.junit.jupiter.api.Test;

/**
 * Full tests for hard version.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class HardCrawlerTest extends EasyCrawlerTest {
    public HardCrawlerTest() {
    }

    @Test
    public void test20_singleConnectionPerHost() {
        test("https://itmo.ru", 2, UNLIMITED, UNLIMITED, 1, MINOR_TIMEOUT, MINOR_TIMEOUT);
    }

    @Test
    public void test21_limitedConnectionsPerHost() {
        test("https://itmo.ru", 2, UNLIMITED, UNLIMITED, 2, MINOR_TIMEOUT, MINOR_TIMEOUT);
    }

    @Test
    public void test22_limitedConnectionsPerformance() {
        testPerformance(2, 3200);
        testPerformance(10, 800);
    }

    private void testPerformance(final int perHost, final double target) {
        checkTime(target, test("http://www.kgeorgiy.info", 3, UNLIMITED, UNLIMITED, perHost, MAJOR_TIMEOUT * 2, MAJOR_TIMEOUT));
    }
}
