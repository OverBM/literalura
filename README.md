# LiteraLura
---
## Descripción
LiteraLura es una aplicación de consola desarrollada en Java con Spring Boot conectada a PostgreSQL,  
la aplicación permite buscar, registrar y consultar libros y autores utilizando la API de Gutendex.

---
## Funcionalidades
- Buscar libro por título.
- Listar libros registrados en la base de datos.
- Listar autores registrados en la base de datos.
- Listar autores vivos en un determinado año.
- Listar libros por idioma.
- Ver estadísticas de descargas de libros.
- TOP 10 libros más descargados.
- Buscar autores por nombre.
- Buscar autores por edad.

---
## Estructura del Proyecto
```
literalura/
├── src/main/java/com/catalogo/literalura/
│   ├── model/
│   │   ├── DatosAutor        # Record para deserializar datos del autor desde la API
│   │   ├── DatosLibro        # Record para deserializar datos del libro desde la API
│   │   ├── DatosResultado    # Record para deserializar la respuesta general de la API
│   │   └── Libro             # Entidad JPA que representa un libro en la base de datos
│   ├── principal/
│   │   └── Principal         # Menú e interacción con el usuario mediante consola
│   ├── repository/
│   │   └── LibroRepository   # Interfaz JPA para consultas a la base de datos
│   ├── service/
│   │   ├── IConvierteDatos   # Interfaz para la conversión de datos JSON
│   │   ├── ConsumoAPI        # Servicio para realizar peticiones HTTP a la API
│   │   └── ConvierteDatos    # Implementación de IConvierteDatos, deserializa JSON con Jackson
│   └── LiteraluraApplication # Clase principal de Spring Boot
├── src/main/resources/
│   └── application.properties # Configuración de la base de datos
└── pom.xml                    # Dependencias del proyecto
```

---
## Requisitos
- Java 17
- Maven
- PostgreSQL
  
## Dependencias
- Spring Boot Starter Data JPA
- PostgreSQL Driver
- Jackson Databind
  
---
## Instalación
1. Clonar el repositorio:
```bash
git clone https://github.com/OverBM/literalura.git
```
2. Crear la base de datos en pgAdmin4 (O descargar el script en el branch ```BD```):
```sql
CREATE DATABASE literalura;
```
3. Configurar las credenciales en `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost/literalura
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```
4. Recargar dependencias Maven
5. Ejecutar el programa

---
## API Utilizada
[Gutendex](https://gutendex.com) - JSON web API para metadata de libros.
