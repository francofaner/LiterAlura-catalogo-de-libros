package com.franco.literalura.principal;

import com.franco.literalura.model.*;
import com.franco.literalura.repository.AutorRepository;
import com.franco.literalura.repository.LibroRepository;
import com.franco.literalura.service.ConsumoAPI;
import com.franco.literalura.service.ConvierteDatos;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private final Scanner teclado = new Scanner(System.in);
    private final ConsumoAPI consumoAPI = new ConsumoAPI();
    private final ConvierteDatos conversor = new ConvierteDatos();
    private final String URL_BASE = "https://gutendex.com/books/?search=";

    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void mostrarMenu() {
        var opcion = -1;

        while (opcion != 0) {
            System.out.println("""
                    
                    ===== LITERALURA =====
                    1 - Buscar libro por título
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma
                    6 - Mostrar cantidad de libros por idioma
                    0 - Salir
                    
                    Elija una opción:
                    """);

            try {
                opcion = Integer.parseInt(teclado.nextLine());

                switch (opcion) {
                    case 1 -> buscarLibroPorTitulo();
                    case 2 -> listarLibrosRegistrados();
                    case 3 -> listarAutoresRegistrados();
                    case 4 -> listarAutoresVivosPorAnio();
                    case 5 -> listarLibrosPorIdioma();
                    case 6 -> mostrarCantidadLibrosPorIdioma();
                    case 0 -> System.out.println("Cerrando LiterAlura...");
                    default -> System.out.println("Opción inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número válido.");
            }
        }
    }

    private void buscarLibroPorTitulo() {
        System.out.println("Escriba el título del libro que desea buscar:");
        var tituloBuscado = teclado.nextLine();

        var tituloCodificado = URLEncoder.encode(tituloBuscado, StandardCharsets.UTF_8);
        var json = consumoAPI.obtenerDatos(URL_BASE + tituloCodificado);
        DatosRespuesta datos = conversor.obtenerDatos(json, DatosRespuesta.class);

        if (datos.resultados() == null || datos.resultados().isEmpty()) {
            System.out.println("No se encontraron libros para esa búsqueda.");
            return;
        }

        DatosLibro datosLibro = datos.resultados().get(0);

        Optional<Libro> libroExistente = libroRepository.findByTituloIgnoreCase(datosLibro.titulo());
        if (libroExistente.isPresent()) {
            System.out.println("Ese libro ya está registrado.");
            System.out.println(libroExistente.get());
            return;
        }

        if (datosLibro.autores() == null || datosLibro.autores().isEmpty()) {
            System.out.println("El libro no tiene autor válido en la respuesta.");
            return;
        }

        DatosAutor datosAutor = datosLibro.autores().get(0);

        Autor autor = autorRepository.findByNombreIgnoreCase(datosAutor.nombre())
                .orElseGet(() -> autorRepository.save(new Autor(datosAutor)));

        Libro libro = new Libro(datosLibro);
        libro.setAutor(autor);

        libroRepository.save(libro);

        System.out.println("Libro guardado exitosamente:");
        System.out.println(libro);
    }

    private void listarLibrosRegistrados() {
        List<Libro> libros = libroRepository.findAll();

        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
            return;
        }

        libros.forEach(System.out::println);
    }

    private void listarAutoresRegistrados() {
        List<Autor> autores = autorRepository.findAll();

        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados.");
            return;
        }

        autores.forEach(autor -> {
            String libros = autor.getLibros().stream()
                    .map(Libro::getTitulo)
                    .collect(Collectors.joining(", "));

            System.out.println("""
                    ----- AUTOR -----
                    Nombre: %s
                    Año de nacimiento: %s
                    Año de fallecimiento: %s
                    Libros: %s
                    """.formatted(
                    autor.getNombre(),
                    autor.getAnioNacimiento(),
                    autor.getAnioFallecimiento(),
                    libros.isBlank() ? "Sin libros" : libros
            ));
        });
    }

    private void listarAutoresVivosPorAnio() {
        System.out.println("Ingrese el año para buscar autores vivos:");
        try {
            int anio = Integer.parseInt(teclado.nextLine());

            List<Autor> vivosConFallecimientoPosterior =
                    autorRepository.findByAnioNacimientoLessThanEqualAndAnioFallecimientoGreaterThanEqual(anio, anio);

            List<Autor> vivosSinFallecimiento =
                    autorRepository.findByAnioNacimientoLessThanEqualAndAnioFallecimientoIsNull(anio);

            Set<Autor> autoresVivos = new LinkedHashSet<>();
            autoresVivos.addAll(vivosConFallecimientoPosterior);
            autoresVivos.addAll(vivosSinFallecimiento);

            if (autoresVivos.isEmpty()) {
                System.out.println("No se encontraron autores vivos en ese año.");
                return;
            }

            autoresVivos.forEach(System.out::println);

        } catch (NumberFormatException e) {
            System.out.println("Debe ingresar un año válido.");
        }
    }

    private void listarLibrosPorIdioma() {
        System.out.println("""
                Ingrese el idioma para buscar:
                es - Español
                en - Inglés
                fr - Francés
                pt - Portugués
                """);

        String codigo = teclado.nextLine().toLowerCase();
        List<Libro> libros = libroRepository.findByIdioma(codigo);

        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros en ese idioma.");
            return;
        }

        libros.forEach(System.out::println);
    }

    private void mostrarCantidadLibrosPorIdioma() {
        System.out.println("""
                Ingrese el idioma para contar:
                es - Español
                en - Inglés
                fr - Francés
                pt - Portugués
                """);

        String codigo = teclado.nextLine().toLowerCase();
        Long cantidad = libroRepository.countByIdioma(codigo);

        Idioma idioma = Idioma.desdeCodigo(codigo);
        String descripcion = idioma != null ? idioma.getDescripcion() : codigo;

        System.out.println("Cantidad de libros en " + descripcion + ": " + cantidad);
    }
}