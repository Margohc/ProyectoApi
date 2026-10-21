package client;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public abstract class BaseRequest implements IRequest {
    protected RequestSpecification request(RequestInfo info) {
        RequestSpecification request = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON);
        if (info.getEmail() != null) {
            request.auth().preemptive().basic(info.getEmail(), info.getPassword());
        }
        return request;
    }

    protected RequestSpecification requestWithBody(RequestInfo info) {
        RequestSpecification request = request(info);
        if (info.getBody() != null) {
            request.body(info.getBody());
        }
        return request;
    }
}
