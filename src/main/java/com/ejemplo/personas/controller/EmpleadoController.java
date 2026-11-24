package com.ejemplo.personas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ejemplo.personas.exception.RecursoNoEncontradoException;
import com.ejemplo.personas.exception.ReglaNegocioException;
import com.ejemplo.personas.model.Empleado;
import com.ejemplo.personas.model.Puesto;
import com.ejemplo.personas.service.DepartamentoService;
import com.ejemplo.personas.service.EmpleadoService;

import jakarta.validation.Valid;

@Controller
public class EmpleadoController {

    private final EmpleadoService service;
    private final DepartamentoService departamentoService;

    public EmpleadoController(EmpleadoService service, DepartamentoService departamentoService) {
        this.service = service;
        this.departamentoService = departamentoService;
    }

    // Datos que necesita el formulario para los dos <select>
    private void cargarListas(Model model, Long departamentoId) {
        model.addAttribute("departamentos", departamentoService.listar());
        model.addAttribute("puestos", Puesto.values());
        model.addAttribute("departamentoId", departamentoId);
    }

    @GetMapping("/empleados")
    public String listar(@RequestParam(required = false) Long departamentoId, Model model) {
        if (departamentoId == null) {
            model.addAttribute("empleados", service.listar());
        } else {
            model.addAttribute("empleados", service.listarPorDepartamento(departamentoId));
        }
        model.addAttribute("departamentos", departamentoService.listar());
        model.addAttribute("departamentoId", departamentoId);
        return "empleados";
    }

    @GetMapping("/empleados/nuevo")
    public String nuevo(@RequestParam(required = false) Long departamentoId, Model model) {
        model.addAttribute("empleado", new Empleado());
        cargarListas(model, departamentoId);
        return "empleado-form";
    }

    @PostMapping("/empleados/guardar")
    public String guardar(@Valid @ModelAttribute Empleado empleado,
                          BindingResult resultado,
                          @RequestParam(required = false) Long departamentoId,
                          Model model, RedirectAttributes ra) {
        if (resultado.hasErrors()) {
            cargarListas(model, departamentoId);
            return "empleado-form";
        }
        try {
            service.crear(empleado, departamentoId);
        } catch (ReglaNegocioException | RecursoNoEncontradoException e) {
            model.addAttribute("error", e.getMessage());
            cargarListas(model, departamentoId);
            return "empleado-form";
        }
        ra.addFlashAttribute("mensaje", "Empleado creado correctamente");
        return "redirect:/empleados";
    }

    @GetMapping("/empleados/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            Empleado empleado = service.buscarPorId(id);
            model.addAttribute("empleado", empleado);
            cargarListas(model, empleado.getDepartamento().getId());
        } catch (RecursoNoEncontradoException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/empleados";
        }
        return "empleado-form";
    }

    @PostMapping("/empleados/{id}/actualizar")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute Empleado empleado,
                             BindingResult resultado,
                             @RequestParam(required = false) Long departamentoId,
                             Model model, RedirectAttributes ra) {
        empleado.setId(id);
        if (resultado.hasErrors()) {
            cargarListas(model, departamentoId);
            return "empleado-form";
        }
        try {
            service.actualizar(id, empleado, departamentoId);
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            cargarListas(model, departamentoId);
            return "empleado-form";
        } catch (RecursoNoEncontradoException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/empleados";
        }
        ra.addFlashAttribute("mensaje", "Empleado actualizado correctamente");
        return "redirect:/empleados";
    }

    @PostMapping("/empleados/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            service.eliminar(id);
            ra.addFlashAttribute("mensaje", "Empleado eliminado correctamente");
        } catch (RecursoNoEncontradoException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/empleados";
    }
}