package com.aluracursos.literatura.principal;

import com.aluracursos.literatura.dto.Datos;
import com.aluracursos.literatura.dto.DatosAutor;
import com.aluracursos.literatura.dto.DatosLibro;
import com.aluracursos.literatura.model.Autor;
import com.aluracursos.literatura.model.Libro;
import com.aluracursos.literatura.repository.AutorRepository;
import com.aluracursos.literatura.repository.LibroRepository;
import com.aluracursos.literatura.service.ConsumoAPI;
import com.aluracursos.literatura.service.ConvierteDatos;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {
    private final LibroRepository libroRepository;
    private final AutorRepository autorRepositorio;

    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private Scanner teclado = new Scanner(System.in);
    private String json;

    private String menu = """
╔═══════════════════════════════════════════╗
║ ¡Bienvenido/a al sistema de libros!       ║
╚═══════════════════════════════════════════╝

Elige la opción a través de los números:

1 - Buscar libro por título.
2 - Listar libros registrados.
3 - Listar autores registrados.
4 - Listar autores vivos en una determinada fecha.
5 - Listar libros por idioma.
6 - Top 10 libros más descargados.
7 - Borrar todos los datos de libros y autores del registro.
0 - Salir.

Escriba el número de la opción elegida:
""";



    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepositorio = autorRepository;
    }

    //Menu de opciones del usuario.
    public void muestraElMenu() {
        var opcionElegida = -1;
        while (opcionElegida != 0) {
            System.out.println(menu);

            String entrada = teclado.nextLine();

            try {
                opcionElegida = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Opción no válida, por favor ingrese un número.");
                continue; // vuelve a mostrar el menú sin romper
            }

            switch (opcionElegida) {
                case 1 -> buscarLibroPorTitulo();
                case 2 -> listarLibrosRegistrados();
                case 3 -> listarAutoresRegistrados();
                case 4 -> listarAutoresVivosEnFecha();
                case 5 -> listarLibrosPorIdioma();
                case 6 -> mostrarTop10LibrosMasDescargados();
                case 7 -> borrarTodosLosDatos();
                case 0 -> System.out.println("Saliendo del sistema...");
                default -> System.out.println("Opción no válida, por favor intente de nuevo.");
            }
        }
    }


//Metodos del menu de opciones del usuario.

//Opcion 1: Buscar libro por titulo.
private void buscarLibroPorTitulo() {
    DatosLibro datosLibro = recibirDatosLibro();
    if (datosLibro != null) {
        DatosAutor datosAutor = datosLibro.autor().get(0);

        List<Autor> autores = autorRepositorio.findByNombre(datosAutor.nombre());
        Autor autorExistente;

        if (!autores.isEmpty()) {
            autorExistente = autores.get(0);
        } else {
            autorExistente = new Autor(datosAutor);
            autorRepositorio.save(autorExistente);
        }

        // Verificamos si ya existe el libro registrado
        List<Libro> librosExistentes = libroRepository.findByTituloAndAutorNombre(
                datosLibro.titulo(), datosAutor.nombre());

        if (!librosExistentes.isEmpty()) {
            System.out.println("El libro ya está registrado:");
            librosExistentes.forEach(System.out::println);
        } else {
            Libro nuevoLibro = new Libro(datosLibro, autorExistente);
            libroRepository.save(nuevoLibro);
            System.out.println("Libro registrado: " + nuevoLibro);
        }

    } else {
        System.out.println("No se encontraron resultados para el libro buscado.");
    }
}


// Metodo para recibir los datos del libro a buscar.
    private DatosLibro recibirDatosLibro() {
        System.out.println("Ingrese el titulo del libro que desea buscar");
        var nombreLibro = teclado.nextLine();
        json = consumoAPI.obtenerDatos(URL_BASE +
                "?search=" + nombreLibro.replace(" ", "+"));
        Datos datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        Optional<DatosLibro> libroBuscado = datosBusqueda.resultados().stream()
                .filter(libro-> libro.titulo().toUpperCase().contains(nombreLibro.toUpperCase()))
                .findFirst();
        if(libroBuscado.isPresent()){
            return libroBuscado.get();
        }else {
            return null;
        }
    }

//opcion 2: Listar libros registrados.
    private void listarLibrosRegistrados() {
        var libros = libroRepository.findAll();

        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados en la base de datos.");
        } else {
            System.out.println("\nListado de libros registrados:");
            libros.forEach(System.out::println);
        }
    }

//Opcion 3: Listar autores registrados.
    private void listarAutoresRegistrados() {
        var autores = autorRepositorio.findAll();

        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados en la base de datos.");
        } else {
            System.out.println("\nListado de autores registrados:");
            autores.forEach(System.out::println);
        }
    }

// Opcion 4: Listar autores vivos en una determinada fecha.
private void listarAutoresVivosEnFecha() {
    System.out.println("Ingrese el año para listar autores vivos en esa fecha:");
    String entrada = teclado.nextLine();

    int anio;
    try {
        anio = Integer.parseInt(entrada);
    } catch (NumberFormatException e) {
        System.out.println("Entrada no válida. Debe ingresar un número.");
        return; // sale del método y vuelve al menú principal
    }

    var autores = autorRepositorio.findAll();

    var autoresVivos = autores.stream()
            .filter(autor -> {
                Integer nacimiento = autor.getFechaDeNacimiento();
                Integer fallecimiento = autor.getFechaDeFallecimiento();

                return nacimiento != null && nacimiento <= anio &&
                        (fallecimiento == null || fallecimiento > anio);
            })
            .toList();

    if (autoresVivos.isEmpty()) {
        System.out.println("No hay autores vivos en el año " + anio);
    } else {
        System.out.println("\nListado de autores vivos en el año " + anio + ":");
        autoresVivos.forEach(System.out::println);
    }
}

    // Opcion 5: Listar libros por idioma.
    private void listarLibrosPorIdioma() {
        System.out.println("Ingrese el idioma para listar los libros (por ejemplo: es, en, fr):");
        String idioma = teclado.nextLine().trim().toLowerCase();

        var libros = libroRepository.findAll();

        var librosFiltrados = libros.stream()
                .filter(libro -> libro.getIdioma() != null && libro.getIdioma().toLowerCase().equals(idioma))
                .toList();

        if (librosFiltrados.isEmpty()) {
            System.out.println("No se encontraron libros en el idioma: " + idioma);
        } else {
            System.out.println("\nListado de libros en idioma '" + idioma + "':");
            librosFiltrados.forEach(System.out::println);
        }
    }

// Opcion 6: Mostrar top 10 libros más descargados.
    private void mostrarTop10LibrosMasDescargados() {
        String url = URL_BASE + "?sort=download_count";
        String jsonRespuesta = consumoAPI.obtenerDatos(url);

        Datos datos = conversor.obtenerDatos(jsonRespuesta, Datos.class);

        List<DatosLibro> top10 = datos.resultados().stream()
                .limit(10)
                .toList();

        System.out.println("\nTop 10 libros más descargados (desde la API): " +
                "\n----------------------");
        for (DatosLibro libro : top10) {
            System.out.println("Título: " + libro.titulo());
            System.out.println("Idioma: " + libro.idiomas());
            System.out.println("Descargas: " + libro.numeroDeDescargas());
            System.out.println("Autor: " + (libro.autor().isEmpty() ? "Desconocido" : libro.autor().get(0).nombre()));
            System.out.println("---------------------");
        }
    }

// Opcion 7: Borrar todos los datos de libros y autores del registro.
    private void borrarTodosLosDatos() {
        libroRepository.deleteAll();
        autorRepositorio.deleteAll();
        System.out.println("Se han borrado todos los libros y autores de la base de registro.");
    }

}

