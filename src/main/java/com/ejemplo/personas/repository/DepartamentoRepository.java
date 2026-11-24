package com.ejemplo.personas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ejemplo.personas.model.Departamento;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}