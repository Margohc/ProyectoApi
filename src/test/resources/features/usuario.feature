# language: en
@api @usuarios
Feature: Gestión de usuarios en Todo.ly
  Como usuario de la API
  Quiero crear, actualizar y eliminar una cuenta de prueba
  Para comprobar el ciclo de vida de un usuario

  Scenario: Crear, actualizar y eliminar un usuario
    Given que preparo datos únicos para un usuario de prueba
    When creo el usuario en Todo.ly
    Then la API confirma la creación y devuelve su ID
    When actualizo el nombre del usuario creado
    Then la API confirma el nuevo nombre
    When elimino el usuario creado
    Then la API confirma que se elimino el usuario
    And el usuario eliminado ya no puede autenticarse
