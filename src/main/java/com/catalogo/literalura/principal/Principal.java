package com.catalogo.literalura.principal;

import com.catalogo.literalura.model.*;
import com.catalogo.literalura.repository.LibroRepository;
import com.catalogo.literalura.service.ConsumoAPI;
import com.catalogo.literalura.service.ConvierteDatos;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.DoubleSummaryStatistics;
import java.util.Scanner;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://gutendex.com/books/";
    private ConvierteDatos conversor = new ConvierteDatos();
    private LibroRepository libroRepository;

    public Principal(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    \n//============ MENU PRINCIPAL ============//
                    
                    1 - Buscar Libro Por Titulo
                    2 - Buscar Libros Registrados
                    3 - Listar Autores Registrados
                    4 - Listar Autores Vivos En Un Determinado Año
                    5 - Listar Libros Por Idioma
                    6 - Ver Estadisticas De Libros
                    7 - TOP 10 Libros Mas Descargados
                    8 - Buscar Autores Por Nombre
                    9 - Buscar Autores Por Edad (Entre 9 Años)
                                  
                    0 - Salir
                    //============ =============== ============//\n
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();
            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivosPorAnio();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 6:
                    estadisticasDesdeDB();
                    estadisticasDesdeAPI();
                    break;
                case 7:
                    top10LibrosMasDescargados();
                    break;
                case 8:
                    buscarAutoresPorNombre();
                    break;
                case 9:
                    buscarAutoresPorEdad();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    private void buscarLibroPorTitulo() {
        System.out.println("Escribe el título del libro a buscar: ");
        var nombreLibro = teclado.nextLine();
        var libroExistente = libroRepository.findByTituloContainsIgnoreCase(nombreLibro);
        if (libroExistente.isPresent()) {
            imprimirLibro(libroExistente.get());
            return;
        }
        mostrarCarga();
        try {
            var urlEncoded = URLEncoder.encode(nombreLibro, StandardCharsets.UTF_8);
            var json = consumoApi.obtenerDatos(URL_BASE + "?search=" + urlEncoded);
            var resultado = conversor.obtenerDatos(json, DatosResultado.class);
            if (resultado.resultados().isEmpty()) {
                System.out.println("Libro no encontrado.");
            } else {
                Libro libro = new Libro(resultado.resultados().get(0));
                libroRepository.save(libro);
                imprimirLibro(libro);
            }
        } catch (Exception e) {
            System.out.println("Error al buscar el libro, intenta de nuevo.");
        }
    }

    private void listarLibrosRegistrados() {
        var libros = libroRepository.findAll();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados aún.");
            return;
        }
        System.out.println("\n--- Libros Registrados ---");
        libros.forEach(l -> imprimirLibro(l));
    }

    private void listarAutoresRegistrados() {
        var libros = libroRepository.findAll();
        if (libros.isEmpty()) {
            System.out.println("No hay autores registrados aún.");
            return;
        }
        System.out.println("\n--- Autores Registrados ---");
        libros.forEach(l -> imprimirAutor(l));
    }

    private void listarAutoresVivosPorAnio() {
        System.out.println("Ingresa el año: ");
        var anio = teclado.nextInt();
        teclado.nextLine();

        var libros = libroRepository.autorVivoPorAnio(anio);
        if (libros.isEmpty()) {
            System.out.println("No se encontraron autores vivos en ese año.");
            return;
        }
        libros.forEach(l -> imprimirAutor(l));
    }

    private void listarLibrosPorIdioma() {
        System.out.println("""
        Ingresa el idioma:
        ------- --------- -------
        [     es - Español     ]
        [     en - Inglés      ]
        [     fr - Francés     ]
        [     pt - Portugués   ]
        ------- --------- -------
        """);
        var idioma = teclado.nextLine();
        var libros = libroRepository.findByIdioma(idioma);
        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros en ese idioma.");
            return;
        }
        libros.forEach(l -> imprimirLibro(l));
    }

    private void estadisticasDesdeAPI() {
        mostrarCarga();
        var json = consumoApi.obtenerDatos(URL_BASE + "?sort=popular");
        var resultado = conversor.obtenerDatos(json, DatosResultado.class);
        DoubleSummaryStatistics estadisticas = resultado.resultados().stream()
                .mapToDouble(l -> l.numeroDeDescargas())
                .summaryStatistics();
        imprimirEstadisticas("--- -- Estadísticas de la API (Top 32 más populares) --- --", estadisticas);
    }

    private void estadisticasDesdeDB() {
        var libros = libroRepository.findAll();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados en la base de datos.");
            return;
        }
        DoubleSummaryStatistics estadisticas = libros.stream()
                .mapToDouble(Libro::getNumeroDeDescargas)
                .summaryStatistics();
        imprimirEstadisticas("--- -- Estadísticas de la Base de Datos --- --", estadisticas);
    }

    private void top10LibrosMasDescargados() {
        var libros = libroRepository.findTop10ByOrderByNumeroDeDescargasDesc();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados aún.");
            return;
        }
        System.out.println("\n--- TOP 10 Libros Más Descargados ---");
        var contador = new int[]{1};
        libros.forEach(l -> {
            System.out.println("\n---TOP [" + contador[0]++ + "]---");
            imprimirLibro(l);
        });
    }

    private void buscarAutoresPorNombre() {
        System.out.println("Ingresa el nombre del autor a buscar: ");
        var nombre = teclado.nextLine();
        var libros = libroRepository.findByAutorContainsIgnoreCase(nombre);
        if (libros.isEmpty()) {
            System.out.println("No se encontraron autores con ese nombre.");
            return;
        }
        System.out.println("\n--- Autores encontrados ---");
        libros.forEach(l -> imprimirAutor(l));
    }

    private void buscarAutoresPorEdad() {
        System.out.println("Ingresa la edad del autor a buscar: ");
        var edad = teclado.nextInt();
        teclado.nextLine();

        var libros = libroRepository.buscarAutoresPorEdad(edad);
        if (libros.isEmpty()) {
            System.out.println("No se encontraron autores que hayan vivido entre " + edad + " y " + (edad + 9) + " años.");
            return;
        }
        System.out.println("\n--- Autores que vivieron entre " + edad + " y " + (edad + 9) + " años ---");
        libros.forEach(l -> imprimirAutor(l));
    }

    private void imprimirLibro(Libro libro) {
        System.out.println("\n--- Libro encontrado ---");
        System.out.println("Título: " + libro.getTitulo());
        System.out.println("Autor: " + libro.getAutor());
        System.out.println("Idioma: " + libro.getIdioma());
        System.out.println("Descargas: " + libro.getNumeroDeDescargas());
        System.out.println("----- ------------- -----\n");
    }

    private void imprimirAutor(Libro l) {
        System.out.println("\n----- ------------- -----");
        System.out.println("Autor: " + l.getAutor());
        System.out.println("Fecha de nacimiento: " + l.getAnioNacimiento());
        System.out.println("Fecha de fallecimiento: " + l.getAnioFallecimiento());
        System.out.println("Libros: " + l.getTitulo());
        System.out.println("----- ------------- -----\n");
    }

    private void imprimirEstadisticas(String titulo, DoubleSummaryStatistics estadisticas) {
        System.out.println("\n--- " + titulo + " ---");
        System.out.println("Total de libros: " + estadisticas.getCount());
        System.out.println("Promedio de descargas: " + String.format("%.2f", estadisticas.getAverage()));
        System.out.println("Máximo de descargas: " + estadisticas.getMax());
        System.out.println("Mínimo de descargas: " + estadisticas.getMin());
        System.out.println("Total de descargas: " + estadisticas.getSum());
        System.out.println("----- ------------- -----\n");
    }

    private void mostrarCarga() {
        System.out.println("\n----- ------------- -----");
        System.out.println("Consultando API, por favor espere...");
        String[] carga = {"\t°", "\t°  °", "\t°  °  °\n"};
        for (String cargando : carga) {
            System.out.print("\r" + cargando);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
            System.out.print("\r");
        }
        System.out.println("----- ------------- -----\n");
    }
}