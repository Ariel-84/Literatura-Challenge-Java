package com.aluracursos.literatura.repository;

import com.aluracursos.literatura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    // Devuelve una lista de libros que coincidan con el título (no uno solo)
    List<Libro> findByTitulo(String titulo);

    // Filtra por idioma ignorando mayúsculas/minúsculas
    List<Libro> findByIdiomaIgnoreCase(String idioma);

    // Busca libros por título y autor exacto
    List<Libro> findByTituloAndAutorNombre(String titulo, String nombre);
}
