package steps;

import client.UserApiClient;
import io.cucumber.java.After;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import io.restassured.response.Response;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserSteps {
    private final UserApiClient client = new UserApiClient(
            System.getenv().getOrDefault("TODO_BASE_URL", "https://todo.ly"));

    private String email;
    private String password;
    private String initialName;
    private String updatedName;
    private Long userId;
    private Response response;
    private boolean created;
    private boolean deleted;

    @Dado("que preparo datos únicos para un usuario de prueba")
    public void prepareUser() {
        email = "apitc" + UUID.randomUUID().toString().replace("_", "") + "@example.com";
        password = "apiTc" + UUID.randomUUID();
        initialName = "Usuario API";
        updatedName = "Usuario API Actualizado";
    }

    @Cuando("creo el usuario en Todo.ly")
    public void createUser() { response = client.createUser(email, password, initialName);
        created = response.statusCode() == 200;
    }


    @Entonces("la API confirma la creación y devuelve su ID")
    public void verifyCreatedUser() {
        assertEquals(200, response.statusCode(), response.asString());
        userId = response.jsonPath().getLong("Id");
        assertNotNull(userId, "La respuesta debe incluir el Id del usuario");
        assertTrue(userId > 0, "El Id del usuario debe ser positivo");
        assertEquals(email, response.jsonPath().getString("Email"));
        assertEquals(initialName, response.jsonPath().getString("FullName"));
    }

    @Cuando("actualizo el nombre del usuario creado")
    public void updateUser() {
        response = client.updateUser(email, password, updatedName);
    }

    @Entonces("la API confirma el nuevo nombre")
    public void verifyUpdatedUser() {
        assertEquals(200, response.statusCode(), response.asString());
        assertEquals(userId, response.jsonPath().getLong("Id"));
        assertEquals(updatedName, response.jsonPath().getString("FullName"));

        Response savedUser = client.getUser(email, password);
        assertEquals(200, savedUser.statusCode(), savedUser.asString());
        assertEquals(updatedName, savedUser.jsonPath().getString("FullName"));
    }

    @Cuando("elimino el usuario creado")
    public void deleteUser() {
        response = client.deleteUser(email, password);
        deleted = response.statusCode() == 200;
    }

    @Entonces("la API confirma que se elimino el usuario")
    public void verifyDeletedUser() {
        assertEquals(200, response.statusCode(), response.asString());
        assertEquals(userId, response.jsonPath().getLong("Id"));
    }

    @Y("el usuario eliminado ya no puede autenticarse")
    public void verifyUserCannotAuthenticate() {
        Response authentication = client.isAuthenticated(email, password);
        assertEquals(200, authentication.statusCode(), authentication.asString());
        assertEquals("false", authentication.asString().trim());
    }

    @After("@usuarios")
    public void deleteUserAfterFailure() {
        if (created && !deleted) {
            Response cleanup = client.deleteUser(email, password);
            if (cleanup.statusCode() != 200) {
                System.err.println("No se pudo limpiar el usuario de prueba: " + email);
            }
        }
    }
}
