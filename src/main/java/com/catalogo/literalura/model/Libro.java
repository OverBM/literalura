package com.catalogo.literalura.model;

import jakarta.persistence.*;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String autor;
    private String idioma;
    private Double numeroDeDescargas;
    private Integer anioNacimiento;
    private Integer anioFallecimiento;

    public Libro() {}

    public Libro(DatosLibro datos) {
        this.titulo = datos.titulo();
        this.autor = datos.autores() != null && !datos.autores().isEmpty() ? datos.autores().get(0).nombre() : "Autor desconocido";
        this.idioma = datos.idioma() != null && !datos.idioma().isEmpty() ? datos.idioma().get(0) : "Desconocido";
        this.numeroDeDescargas = datos.numeroDeDescargas();
        this.anioNacimiento = datos.autores() != null && !datos.autores().isEmpty() ? datos.autores().get(0).anioNacimiento() : null;
        this.anioFallecimiento = datos.autores() != null && !datos.autores().isEmpty() ? datos.autores().get(0).anioFallecimiento() : null;
    }

    public Integer getAnioNacimiento() {
        return anioNacimiento;
    }

    public void setAnioNacimiento(Integer anioNacimiento) {
        this.anioNacimiento = anioNacimiento;
    }

    public Integer getAnioFallecimiento() {
        return anioFallecimiento;
    }

    public void setAnioFallecimiento(Integer anioFallecimiento) {
        this.anioFallecimiento = anioFallecimiento;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public Double getNumeroDeDescargas() {
        return numeroDeDescargas;
    }

    public void setNumeroDeDescargas(Double numeroDeDescargas) {
        this.numeroDeDescargas = numeroDeDescargas;
    }
}
