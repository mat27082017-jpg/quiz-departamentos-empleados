package com.ejemplo.personas.service;

import com.ejemplo.personas.exception.RecursoNoEncontradoException;
import com.ejemplo.personas.exception.ReglaNegocioException;
import com.ejemplo.personas.model.Departamento;
import com.ejemplo.personas.repository.DepartamentoRepository;
import com.ejemplo.personas.repository.EmpleadoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DepartamentoService {

    private final DepartamentoRepository repo;
    private final EmpleadoRepository empleadoRepo;

    public DepartamentoService(DepartamentoRepository repo, EmpleadoRepository empleadoRepo) {
        this.repo = repo;
        this.empleadoRepo = empleadoRepo;
    }

    public List<Departamento> listar() {
        return repo.findAll();
    }

    public Departamento buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Departamento no encontrado"));
    }

    // Suma de los salarios de los empleados del departamento
    public BigDecimal planilla(Long departamentoId) {
        return empleadoRepo.sumarSalariosPorDepartamento(departamentoId);
    }

    @Transactional
    public Departamento crear(Departamento departamento) {
        if (repo.existsByNombreIgnoreCase(departamento.getNombre())) {
            throw new ReglaNegocioException("Ya existe un departamento con ese nombre");
        }
        departamento.setId(null);
        return repo.save(departamento);
    }

    @Transactional
    public Departamento actualizar(Long id, Departamento datos) {
        Departamento existente = buscarPorId(id);

        if (repo.existsByNombreIgnoreCaseAndIdNot(datos.getNombre(), id)) {
            throw new ReglaNegocioException("Ya existe un departamento con ese nombre");
        }
        if (datos.getPresupuesto().compareTo(planilla(id)) < 0) {
            throw new ReglaNegocioException("El presupuesto no puede ser menor a la planilla actual");
        }

        existente.setNombre(datos.getNombre());
        existente.setUbicacion(datos.getUbicacion());
        existente.setPresupuesto(datos.getPresupuesto());
        return repo.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        Departamento existente = buscarPorId(id);
        if (!existente.getEmpleados().isEmpty()) {
            throw new ReglaNegocioException("No se puede eliminar un departamento con empleados asignados");
        }
        repo.delete(existente);
    }
}