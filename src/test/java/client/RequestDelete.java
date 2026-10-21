package client;
import io.restassured.response.Response;

public class RequestDelete extends BaseRequest {
    @Override
    public Response send(RequestInfo info) {
        return request(info).delete(info.getUrl());
    }
}
