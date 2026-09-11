package info.kgeorgiy.java.advanced.hello;

import info.kgeorgiy.java.advanced.base.BaseTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Full tests for {@link HelloServer}.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class HelloServerTest extends BaseTest {
    private static final InetSocketAddress SOCKET_ADDRESS = new InetSocketAddress("localhost", 28800);
    private static final int FLOOD_REQUESTS = 10_000;
    public static final String REQUEST = HelloServerTest.class.getName();

    public HelloServerTest() {
    }

    @Test
    public void test01_singleRequest() throws IOException {
        testSocket(1, socket -> checkResponse(socket, REQUEST));
    }

    @Test
    public void test02_multipleClients() throws IOException {
        testServer(1, server -> {
            for (int i = 0; i < 10; i++) {
                client(REQUEST + i);
            }
        });
    }

    @Test
    public void test03_multipleRequests() throws IOException {
        testSocket(1, socket -> {
            for (int i = 0; i < 10; i++) {
                checkResponse(socket, REQUEST + i);
            }
        });
    }

    @Test
    public void test04_parallelRequests() throws IOException {
        testSocket(1, socket -> {
            final Set<String> responses = new HashSet<>();
            for (int i = 0; i < 10; i++) {
                final String request = REQUEST + i;
                responses.add(Util.response(request));
                send(socket, request);
            }
            for (int i = 0; i < 10; i++) {
                final String response = Util.receive(socket);
                Assertions.assertTrue(responses.remove(response), "Unexpected response " + response);
            }
        });
    }

    @Test
    public void test05_parallelClients() throws IOException {
        testServer(1, server -> parallel(10, () -> client(REQUEST)));
    }

    @Test
    public void test06_dos() throws IOException {
        testSocket(3, socket -> parallel(10, () -> {
            for (int i = 0; i < FLOOD_REQUESTS; i++) {
                send(socket, REQUEST);
            }
        }));
    }

    @Test
    public void test07_noDoS() throws IOException {
        testServer(5, server -> parallel(5, () -> {
            try (final DatagramSocket socket = new DatagramSocket(null)) {
                for (int i = 0; i < FLOOD_REQUESTS; i++) {
                    checkResponse(socket, REQUEST + i);
                }
            }
        }));
    }

    @Test
    public void test10_noPorts() throws IOException {
        testServer(List.of());
    }

    @Test
    public void test11_onePort() throws IOException {
        testServer(List.of("Bye %%"));
    }

    @Test
    public void test12_twoPorts() throws IOException {
        testServer(List.of("Hello %%", "Bye %%"));
    }

    @Test
    public void test13_weirdFormat() throws IOException {
        testServer(List.of("%%", "qqq%%", "%%qqq", "$1%%%%", "%%qqq%%"));
    }

    @Test
    public void test14_manyPorts() throws IOException {
        testServer(new Random(787408237408347038L)
                .ints(10)
                .boxed()
                .flatMap(i -> Stream.of(i + "%%", "%%" + i, i + "%%" + i, "%%" + i + "%%"))
                .toList());
    }

    private static void testServer(final List<String> formats) throws IOException {
        final List<Endpoint> endpoints = IntStream.range(0, formats.size())
                .mapToObj(i -> new Endpoint(new InetSocketAddress(SOCKET_ADDRESS.getHostName(), SOCKET_ADDRESS.getPort() + i), formats.get(i)))
                .toList();
        final Map<Integer, String> ports = endpoints.stream()
                .collect(Collectors.toMap(e -> e.address.getPort(), Endpoint::format));
        try (final NewHelloServer server = createCUT()) {
            server.start(1, ports);
            try (final DatagramSocket socket = new DatagramSocket()) {
                for (final Endpoint endpoint : endpoints) {
                    endpoint.check(socket);
                }
            }
        }
    }

    private record Endpoint(InetSocketAddress address, String format) {
        private void check(final DatagramSocket socket) throws IOException {
            final String request = HelloServerTest.REQUEST + ":" + address.getPort();
            final String response = Util.request(request, socket, address);
            Assertions.assertEquals(Util.response(request, format), response, "Invalid response");
        }
    }

    private static void testServer(
            final int threads,
            final ConsumerCommand<HelloServer, IOException> action
    ) throws IOException {
        try (final HelloServer server = createCUT()) {
            server.start(SOCKET_ADDRESS.getPort(), threads);
            action.run(server);
        }
    }

    private static void send(final DatagramSocket socket, final String request) throws IOException {
        Util.send(socket, request, SOCKET_ADDRESS);
    }

    private static void client(final String request) throws IOException {
        try (final DatagramSocket socket = new DatagramSocket(null)) {
            checkResponse(socket, request);
        }
    }

    public static void testSocket(final int workers, final ConsumerCommand<DatagramSocket, IOException> command) throws IOException {
        testServer(workers, server -> {
            try (final DatagramSocket socket = new DatagramSocket()) {
                command.run(socket);
            }
        });
    }

    private static void checkResponse(final DatagramSocket socket, final String request) throws IOException {
        final String response = Util.request(request, socket, SOCKET_ADDRESS);
        Assertions.assertEquals(Util.response(request), response, "Invalid response");
    }
}
