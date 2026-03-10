# LiterAlura-catalogo-de-libros

LiterAlura es una aplicación de consola desarrollada en **Java + Spring Boot** que permite consultar libros desde la API **Gutendex** y guardar la información en una base de datos **PostgreSQL**.

Este proyecto fue realizado como parte del challenge **LiterAlura** de **Alura Latam / Oracle Next Education**.

---

## Funcionalidades

- Buscar libro por título en la API Gutendex
- Guardar libro y autor en la base de datos
- Listar libros registrados
- Listar autores registrados
- Listar autores vivos en un determinado año
- Listar libros por idioma
- Mostrar cantidad de libros por idioma

---

## Tecnologías utilizadas

- Java 17
- Spring Boot 3.2.3
- Spring Data JPA
- PostgreSQL
- Maven
- Jackson
- IntelliJ IDEA

---

## Requisitos para ejecutar el proyecto

Antes de ejecutar la aplicación, es necesario tener instalado:

- Java 17 o superior
- PostgreSQL
- Git

No es obligatorio tener Maven instalado globalmente si el proyecto incluye:

- `mvnw`
- `mvnw.cmd`
- carpeta `.mvn`

---

## Base de datos

Crear una base de datos en PostgreSQL con el siguiente nombre:

```sql
CREATE DATABASE literalura;
