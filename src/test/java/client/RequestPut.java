package client;

import io.restassured.response.Response;

public class RequestPut extends BaseRequest {
    @Override
    public Response send(RequestInfo info) {
        return requestWithBody(info).put(info.getUrl());
    }
}
