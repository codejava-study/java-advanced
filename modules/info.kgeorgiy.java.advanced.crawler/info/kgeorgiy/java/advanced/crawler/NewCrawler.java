package info.kgeorgiy.java.advanced.crawler;

import java.util.List;

/**
 * Crawls websites, filtering <a href="http://tools.ietf.org/html/rfc3986">URLs</a> by substrings.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface NewCrawler extends Crawler {
    /**
     * Downloads website up to specified depth.
     *
     * @param url start URL.
     * @param depth download depth.
     * @param includes only URLs containing one of given substrings are traversed.
     * @return download result.
     */
    Result download(String url, int depth, List<String> includes);

    @Override
    default Result download(final String url, final int depth) {
        return download(url, depth, List.of(""));
    }
}
