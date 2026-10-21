package client;

import java.util.Locale;

public final class FactoryRequest {
    private FactoryRequest() {
    }

    public static IRequest make(String method) {
        if (method == null) {
            throw new IllegalArgumentException("El método HTTP no puede ser null");
        }
        return switch (method.toUpperCase(Locale.ROOT)) {
            case "GET" -> new RequestGet();
            case "POST" -> new RequestPost();
            case "PUT" -> new RequestPut();
            case "DELETE" -> new RequestDelete();
            default -> throw new IllegalArgumentException("Método HTTP no soportado: " + method);
        };
    }
}
