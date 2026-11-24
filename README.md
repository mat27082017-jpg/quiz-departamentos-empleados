# Quiz: Departamentos y Empleados

**Estudiante:** Mathew Arley Castro
**Curso:** Programación de Lenguajes de Cuarta Generación (Programación IV)
**Universidad:** Universidad Central

## Tecnologías
Java 21, Spring Boot 4.1.3, Maven, Spring Data JPA, H2 (en memoria), Spring Security, Thymeleaf, Bootstrap 5.

## Cómo compilar y ejecutar
En PowerShell, dentro de esta carpeta:

    .\mvnw clean compile
    .\mvnw spring-boot:run

La aplicación queda en http://localhost:8080

## Usuarios de prueba
| Usuario | Contraseña | Rol   |
|---------|------------|-------|
| admin   | admin123   | ADMIN |
| user    | user123    | USER  |

- ADMIN: puede crear, editar y eliminar departamentos, empleados y usuarios.
- USER: solo consulta. Si entra a una ruta de ADMIN recibe 403.

## Consola H2
http://localhost:8080/h2-console

- JDBC URL: `jdbc:h2:mem:empresadb`
- Usuario: `sa`
- Contraseña: (vacía)

## Reglas de negocio
- No se permite un departamento con nombre repetido.
- No se permite una cédula repetida (9 dígitos).
- El salario de un empleado no puede exceder el presupuesto disponible del departamento.
- El presupuesto no puede ser menor a la planilla actual.
- No se puede eliminar un departamento con empleados asignados.
- Un empleado puede cambiarse de departamento; la planilla de ambos se recalcula.

## Evidencias
Ver `Evidencias_Quiz_Departamentos_Empleados.pdf` (9 casos con capturas).