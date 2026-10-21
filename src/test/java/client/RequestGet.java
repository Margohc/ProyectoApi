package client;

import io.restassured.response.Response;

public class RequestGet extends BaseRequest {
    @Override
    public Response send(RequestInfo info) {
        return request(info).get(info.getUrl());
    }
}
