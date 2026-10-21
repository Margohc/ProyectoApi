package client;

public class RequestInfo {
    private final String url;
    private final Object body;
    private final String email;
    private final String password;

    public RequestInfo(String url, Object body) {
        this(url, body, null, null);
    }

    public RequestInfo(String url, Object body, String email, String password) {
        if ((email == null) != (password == null)) {
            throw new IllegalArgumentException("El correo y la contraseña deben proporcionarse juntos");
        }
        this.url = url;
        this.body = body;
        this.email = email;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public Object getBody() {
        return body;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
