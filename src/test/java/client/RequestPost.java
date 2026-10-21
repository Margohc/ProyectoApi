package client;

import io.restassured.response.Response;

public class RequestPost extends BaseRequest {
    @Override
    public Response send(RequestInfo info) {
        return requestWithBody(info).post(info.getUrl());
    }
}
