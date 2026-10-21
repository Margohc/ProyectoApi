package client;

import com.sun.net.httpserver.HttpServer;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class FactoryRequestTest {
    @Test
    void selectsImplementationByHttpMethod() {
        assertInstanceOf(RequestGet.class, FactoryRequest.make("get"));
        assertInstanceOf(RequestPost.class, FactoryRequest.make("POST"));
        assertInstanceOf(RequestPut.class, FactoryRequest.make("PUT"));
        assertInstanceOf(RequestDelete.class, FactoryRequest.make("DELETE"));
    }

    @Test
    void rejectsUnsupportedMethodsAndIncompleteCredentials() {
        assertThrows(IllegalArgumentException.class, () -> FactoryRequest.make("PATCH"));
        assertThrows(IllegalArgumentException.class, () -> FactoryRequest.make(null));
        assertThrows(IllegalArgumentException.class,
                () -> new RequestInfo("http://localhost", null, "test@example.com", null));
        assertThrows(IllegalArgumentException.class,
                () -> new RequestInfo("http://localhost", null, null, "local-password"));
    }

    @Test
    void sendsUserOperationsThroughFactoryToLocalServer() throws Exception {
        BlockingQueue<CapturedRequest> requests = new LinkedBlockingQueue<>();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            try {
                requests.add(new CapturedRequest(exchange.getRequestMethod(),
                        exchange.getRequestURI().getPath(),
                        exchange.getRequestHeaders().getFirst("Authorization"),
                        exchange.getRequestHeaders().getFirst("Content-Type"),
                        exchange.getRequestHeaders().getFirst("Accept"),
                        new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8)));
                byte[] body = "{\"Id\":123}".getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, body.length);
                exchange.getResponseBody().write(body);
            } finally {
                exchange.close();
            }
        });
        server.start();
        try {
            UserApiClient client = new UserApiClient("http://127.0.0.1:"
                    + server.getAddress().getPort() + "/");
            String email = "local-test@example.com";
            String password = "local-password";
            String authorization = "Basic " + Base64.getEncoder().encodeToString(
                    (email + ":" + password).getBytes(StandardCharsets.UTF_8));

            verifyResponse(client.createUser(email, password, "Usuario API"));
            CapturedRequest create = verifyRequest(requests, "POST", "/api/user.json", null);
            JsonPath createBody = new JsonPath(create.body());
            assertEquals(email, createBody.getString("Email"));
            assertEquals(password, createBody.getString("Password"));
            assertEquals("Usuario API", createBody.getString("FullName"));

            verifyResponse(client.getUser(email, password));
            assertEquals("", verifyRequest(requests, "GET", "/api/user.json", authorization).body());

            verifyResponse(client.updateUser(email, password, "Actualizado"));
            CapturedRequest update = verifyRequest(requests, "PUT", "/api/user/0.json", authorization);
            assertEquals("Actualizado", new JsonPath(update.body()).getString("FullName"));

            verifyResponse(client.deleteUser(email, password));
            assertEquals("", verifyRequest(requests, "DELETE", "/api/user/0.json", authorization).body());

            verifyResponse(client.isAuthenticated(email, password));
            assertEquals("", verifyRequest(requests, "GET",
                    "/api/authentication/isauthenticated.json", authorization).body());
        } finally {
            server.stop(0);
        }
    }

    private static void verifyResponse(Response response) {
        assertEquals(200, response.statusCode());
        assertEquals(123, response.jsonPath().getInt("Id"));
    }

    private static CapturedRequest verifyRequest(BlockingQueue<CapturedRequest> requests,
                                                String method, String path, String authorization)
            throws InterruptedException {
        CapturedRequest request = requests.poll(5, TimeUnit.SECONDS);
        assertNotNull(request, "El servidor local debe recibir la petición");
        assertEquals(method, request.method());
        assertEquals(path, request.path());
        assertEquals(authorization, request.authorization());
        assertNotNull(request.contentType());
        assertTrue(request.contentType().startsWith("application/json"));
        assertNotNull(request.accept());
        assertTrue(Arrays.stream(request.accept().split(","))
                .anyMatch(type -> type.trim().equals("application/json")));
        return request;
    }

    private record CapturedRequest(String method, String path, String authorization,
                                   String contentType, String accept, String body) {
    }
}
