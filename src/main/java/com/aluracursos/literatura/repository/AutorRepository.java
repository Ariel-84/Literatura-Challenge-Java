package com.aluracursos.literatura.repository;

import com.aluracursos.literatura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    List<Autor> findByNombre(String nombre);

    @Query("SELECT a FROM Autor a WHERE a.fechaDeNacimiento <= :anio AND (a.fechaDeFallecimiento IS NULL OR a.fechaDeFallecimiento > :anio)")
    List<Autor> findAutoresVivosEn(int anio);
}

