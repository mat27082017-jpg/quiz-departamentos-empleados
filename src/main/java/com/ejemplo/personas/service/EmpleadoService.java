package com.ejemplo.personas.service;


import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ejemplo.personas.exception.RecursoNoEncontradoException;
import com.ejemplo.personas.exception.ReglaNegocioException;
import com.ejemplo.personas.model.Departamento;
import com.ejemplo.personas.model.Empleado; 
import com.ejemplo.personas.repository.EmpleadoRepository;

@Service
public class EmpleadoService {

    private final EmpleadoRepository repo;
    private final DepartamentoService departamentoService;

    public EmpleadoService(EmpleadoRepository repo, DepartamentoService departamentoService) {
        this.repo = repo;
        this.departamentoService = departamentoService;
    }

    public List<Empleado> listar() {
        return repo.findAll();
    }

    public List<Empleado> listarPorDepartamento(Long departamentoId) {
        return repo.findByDepartamentoId(departamentoId);
    }

    public Empleado buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empleado no encontrado"));
    }

    private Departamento obtenerDepartamento(Long departamentoId) {
        if (departamentoId == null) {
            throw new ReglaNegocioException("Debe seleccionar un departamento");
        }
        return departamentoService.buscarPorId(departamentoId);
    }

    // planillaOtros = lo que ya suman los demas empleados del departamento
    private void validarPresupuesto(Departamento departamento, BigDecimal planillaOtros, BigDecimal salario) {
        if (planillaOtros.add(salario).compareTo(departamento.getPresupuesto()) > 0) {
            throw new ReglaNegocioException("El salario excede el presupuesto disponible del departamento");
        }
    }

    @Transactional
    public Empleado crear(Empleado empleado, Long departamentoId) {
        Departamento departamento = obtenerDepartamento(departamentoId);

        if (repo.existsByCedula(empleado.getCedula())) {
            throw new ReglaNegocioException("La cédula ya está registrada");
        }
        validarPresupuesto(departamento, departamentoService.planilla(departamentoId), empleado.getSalario());

        empleado.setId(null);
        departamento.agregarEmpleado(empleado);
        return repo.save(empleado);
    }

    @Transactional
    public Empleado actualizar(Long id, Empleado datos, Long departamentoId) {
        Empleado existente = buscarPorId(id);
        Departamento nuevo = obtenerDepartamento(departamentoId);

        if (repo.existsByCedulaAndIdNot(datos.getCedula(), id)) {
            throw new ReglaNegocioException("La cédula ya está registrada");
        }

        // Si sigue en el mismo departamento, su salario actual no cuenta dos veces
        BigDecimal planillaOtros = departamentoService.planilla(departamentoId);
        boolean cambiaDeDepartamento = !existente.getDepartamento().getId().equals(departamentoId);
        if (!cambiaDeDepartamento) {
            planillaOtros = planillaOtros.subtract(existente.getSalario());
        }
        validarPresupuesto(nuevo, planillaOtros, datos.getSalario());

        existente.setNombre(datos.getNombre());
        existente.setCedula(datos.getCedula());
        existente.setEmail(datos.getEmail());
        existente.setPuesto(datos.getPuesto());
        existente.setSalario(datos.getSalario());
        existente.setFechaIngreso(datos.getFechaIngreso());

        if (cambiaDeDepartamento) {
            existente.getDepartamento().quitarEmpleado(existente);
            nuevo.agregarEmpleado(existente);
        }
        return repo.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        Empleado existente = buscarPorId(id);
        existente.getDepartamento().quitarEmpleado(existente);
        repo.delete(existente);
    }
}