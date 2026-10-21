package client;

import io.restassured.response.Response;

import java.util.Map;

public class UserApiClient {
    private final String baseUrl;

    public UserApiClient(String baseUrl) {
        this.baseUrl = baseUrl.replaceAll("/+$", "");
    }

    public Response createUser(String email, String password, String fullName) {
        RequestInfo info = new RequestInfo(baseUrl + "/api/user.json",
                Map.of("Email", email, "Password", password, "FullName", fullName));
        return FactoryRequest.make("POST").send(info);
    }

    public Response getUser(String email, String password) {
        RequestInfo info = new RequestInfo(baseUrl + "/api/user.json", null, email, password);
        return FactoryRequest.make("GET").send(info);
    }

    public Response updateUser(String email, String password, String fullName) {
        RequestInfo info = new RequestInfo(baseUrl + "/api/user/0.json",
                Map.of("FullName", fullName), email, password);
        return FactoryRequest.make("PUT").send(info);
    }

    public Response deleteUser(String email, String password) {
        RequestInfo info = new RequestInfo(baseUrl + "/api/user/0.json", null, email, password);
        return FactoryRequest.make("DELETE").send(info);
    }

    public Response isAuthenticated(String email, String password) {
        RequestInfo info = new RequestInfo(baseUrl + "/api/authentication/isauthenticated.json",
                null, email, password);
        return FactoryRequest.make("GET").send(info);
    }
}
