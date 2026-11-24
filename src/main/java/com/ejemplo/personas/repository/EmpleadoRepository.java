package com.ejemplo.personas.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ejemplo.personas.model.Empleado;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    @Override
    @EntityGraph(attributePaths = "departamento")
    List<Empleado> findAll();

    @EntityGraph(attributePaths = "departamento")
    List<Empleado> findByDepartamentoId(Long departamentoId);

    boolean existsByCedula(String cedula);

    boolean existsByCedulaAndIdNot(String cedula, Long id);

    // Suma de salarios de un departamento (0 si no tiene empleados)
    @Query("select coalesce(sum(e.salario), 0) from Empleado e where e.departamento.id = :departamentoId")
    BigDecimal sumarSalariosPorDepartamento(@Param("departamentoId") Long departamentoId);
}