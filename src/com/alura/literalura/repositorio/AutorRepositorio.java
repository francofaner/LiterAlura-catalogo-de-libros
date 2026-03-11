package com.franco.literalura.repository;

import com.franco.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findByNombreIgnoreCase(String nombre);

    List<Autor> findByAnioNacimientoLessThanEqualAndAnioFallecimientoGreaterThanEqual(Integer anioNacimiento, Integer anioFallecimiento);

    List<Autor> findByAnioNacimientoLessThanEqualAndAnioFallecimientoIsNull(Integer anioNacimiento);
}