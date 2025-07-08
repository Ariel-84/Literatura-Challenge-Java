package com.aluracursos.literatura.model;

import com.aluracursos.literatura.dto.DatosAutor;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "autores")
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private Integer fechaDeNacimiento;
    private Integer fechaDeFallecimiento;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Libro> libros = new ArrayList<>();

    public Autor() {}

    public Autor(DatosAutor datosAutor) {
        this.nombre = String.valueOf(datosAutor.nombre());
        this.fechaDeNacimiento = datosAutor.fechaDeNacimiento();
        this.fechaDeFallecimiento = datosAutor.fechaDeFallecimiento();
    }

    // Getters y setters

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Integer getFechaDeNacimiento() {
        return fechaDeNacimiento;
    }

    public Integer getFechaDeFallecimiento() {
        return fechaDeFallecimiento;
    }

    public List<Libro> getLibros() {
        return libros;
    }

    public void setLibros(List<Libro> libros) {
        this.libros = libros;
    }

    @Override
    public String toString() {
        return "\n----------------------\n" +
                "Autor: " + nombre + "\n" +
                "Nacimiento: " + (fechaDeNacimiento != null ? fechaDeNacimiento : "Desconocido") + "\n" +
                "Fallecimiento: " + (fechaDeFallecimiento != null ? fechaDeFallecimiento : "Vive o desconocido") +
                "\n----------------------";
    }



}
