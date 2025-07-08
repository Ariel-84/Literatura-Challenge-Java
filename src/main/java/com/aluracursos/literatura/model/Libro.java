package com.aluracursos.literatura.model;

import com.aluracursos.literatura.dto.DatosLibro;
import jakarta.persistence.*;

@Entity
@Table(name = "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String idioma;
    private Double numeroDeDescargas;

    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Autor autor;

    public Libro() {}

    public Libro(DatosLibro datos, Autor autor) {
        this.titulo = datos.titulo();
        this.idioma = datos.idiomas().get(0); // Asumiendo que el primer idioma es el principal
        this.numeroDeDescargas = datos.numeroDeDescargas();
        this.autor = autor;
    }

    // Getters y setters

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getIdioma() {
        return idioma;
    }

    public Double getNumeroDeDescargas() {
        return numeroDeDescargas;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }
    @Override
    public String toString() {
        return "\n----------------------\n" +
                "Libro registrado:\n" +
                "Título: " + titulo + "\n" +
                "Idioma: " + idioma + "\n" +
                "Descargas: " + numeroDeDescargas + "\n" +
                "Autor: " + (autor != null ? autor.getNombre() : "Desconocido") +
                "\n----------------------\n";
    }


}

