package com.catalogo.literalura.repository;

import com.catalogo.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    Optional<Libro> findByTituloContainsIgnoreCase(String titulo);

    @Query("SELECT l FROM Libro l WHERE l.anioNacimiento <= :anio AND (l.anioFallecimiento IS NULL OR l.anioFallecimiento >= :anio)")
    List<Libro> autorVivoPorAnio(@Param("anio") int anio);

    List<Libro> findByIdioma(String idioma);

    List<Libro> findTop10ByOrderByNumeroDeDescargasDesc();

    List<Libro> findByAutorContainsIgnoreCase(String nombre);

    @Query("SELECT l FROM Libro l WHERE (l.anioFallecimiento - l.anioNacimiento) BETWEEN :edad AND (:edad + 9)")
    List<Libro> buscarAutoresPorEdad(@Param("edad") int edad);
}