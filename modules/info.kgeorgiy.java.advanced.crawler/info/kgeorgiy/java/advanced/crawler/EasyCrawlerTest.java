package info.kgeorgiy.java.advanced.crawler;

import info.kgeorgiy.java.advanced.base.BaseTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.ToLongBiFunction;

/**
 * Full tests for easy version.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class EasyCrawlerTest extends BaseTest {
    private static final String SLOW = ".*(erformance|deep|Both)\\(\\)";

    protected static final int UNLIMITED = 100;
    protected static final int MINOR_TIMEOUT = 10;
    protected static final int MAJOR_TIMEOUT = 50;
    protected static final int REAL_TIMEOUT = -3;

    private static final List<List<String>> INCLUDES = List.of(
            List.of("/"),
            List.of("h"),
            List.of("a", "b", "c", "d"),
            List.of("http://", "https://"),
            List.of()
    );

    final ToLongBiFunction<CrawlerTask, String> test = EasyCrawlerTest::testTask;

    public EasyCrawlerTest() {
    }

    protected static long testTask(final CrawlerTask task, final String testName) {
        final List<List<String>> tests = testName.matches(SLOW) ? List.of(List.of("")) : INCLUDES;
        return tests.stream().mapToLong(test -> {
            try {
                return task.test(
                        NewCrawler.class,
                        crawler -> crawler.download(task.url(), task.depth(), test),
                        urls -> test.stream().anyMatch(urls::contains)
                );
            } catch (final AssertionError e) {
                throw new AssertionError(
                        "url = %s, depth = %s, includes = %s".formatted(task.url(), task.depth(), test),
                        e
                );
            }
        }).max().orElse(0);
    }

    @Test
    public void test01_singlePage() {
        test("https://en.itmo.ru/en/page/50/Partnership.htm", 1);
        test("https://bars.itmo.ru", 1);
    }

    @Test
    public void test02_pageAndLinks() {
        test("https://itmo.ru", 2);
        test("https://www.itmo.ru", 3);
    }

    @Test
    public void test03_invalid() {
        test("https://itmo.ru/ru/educational-activity/voprosi_predlojeniya.htmvoprosy_i_predlozheniya.htmvoprosy_i_predlozheniya.htm", 1);
    }

    @Test
    public void test04_deep() {
        for (int i = 1; i <= 5; i++) {
            test("http://www.kgeorgiy.info", i);
        }
    }

    @Test
    public void test05_noLimits() {
        test(UNLIMITED, UNLIMITED, MINOR_TIMEOUT, MINOR_TIMEOUT);
    }

    @Test
    public void test06_limitDownloads() {
        test(10, UNLIMITED, MAJOR_TIMEOUT, MINOR_TIMEOUT);
    }

    @Test
    public void test07_limitExtractors() {
        test(UNLIMITED, 10, MINOR_TIMEOUT, MAJOR_TIMEOUT);
    }

    @Test
    public void test08_limitBoth() {
        test(10, 10, MAJOR_TIMEOUT, MAJOR_TIMEOUT);
    }

    @Test
    public void test09_performance() {
        checkTime(1600, test(UNLIMITED, UNLIMITED, 200, 200));
    }

    @Test
    public void test10_realTimePerformance() {
        checkTime(3600, test(UNLIMITED, UNLIMITED, REAL_TIMEOUT, REAL_TIMEOUT));
    }

    protected static void checkTime(final double target, final long time) {
        System.err.println("Time: " + time);
        Assertions.assertTrue(time > 0.8 * target, "Too parallel: " + time);
        Assertions.assertTrue(time < 1.2 * target, "Not parallel: " + time);
    }

    private void test(final String url, final int depth) {
        test(url, depth, UNLIMITED, UNLIMITED, UNLIMITED, MINOR_TIMEOUT, MINOR_TIMEOUT);
    }

    protected final long test(
            final int downloaders,
            final int extractors,
            final int downloadTimeout,
            final int extractTimeout
    ) {
        final String url = "http://nerc.itmo.ru/subregions/index.html";
        return test(url, 3, downloaders, extractors, UNLIMITED, downloadTimeout, extractTimeout);
    }

    protected final long test(
            final String url,
            final int depth,
            final int downloaders,
            final int extractors,
            final int perHost,
            final int downloadTimeout,
            final int extractTimeout
    ) {
        return test.applyAsLong(
                new CrawlerTask(url, depth, downloaders, extractors, perHost, downloadTimeout, extractTimeout),
                testName
        );
    }

    protected interface CrawlerMethod<C extends Crawler> {
        Result download(C crawler, String url, int depth, List<String> test);
    }
}
