package com.ejemplo.personas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ejemplo.personas.exception.RecursoNoEncontradoException;
import com.ejemplo.personas.exception.ReglaNegocioException;
import com.ejemplo.personas.model.Departamento;
import com.ejemplo.personas.service.DepartamentoService;

import jakarta.validation.Valid;

@Controller
public class DepartamentoController {

    private final DepartamentoService service;

    public DepartamentoController(DepartamentoService service) {
        this.service = service;
    }

    @GetMapping("/departamentos")
    public String listar(Model model) {
        model.addAttribute("departamentos", service.listar());
        return "departamentos";
    }

    @GetMapping("/departamentos/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("departamento", new Departamento());
        return "departamento-form";
    }

    @PostMapping("/departamentos/guardar")
    public String guardar(@Valid @ModelAttribute Departamento departamento,
                          BindingResult resultado,
                          Model model, RedirectAttributes ra) {
        if (resultado.hasErrors()) {
            return "departamento-form";
        }
        try {
            service.crear(departamento);
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            return "departamento-form";
        }
        ra.addFlashAttribute("mensaje", "Departamento creado correctamente");
        return "redirect:/departamentos";
    }

    @GetMapping("/departamentos/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("departamento", service.buscarPorId(id));
        } catch (RecursoNoEncontradoException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/departamentos";
        }
        return "departamento-form";
    }

    @PostMapping("/departamentos/{id}/actualizar")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute Departamento departamento,
                             BindingResult resultado,
                             Model model, RedirectAttributes ra) {
        departamento.setId(id);
        if (resultado.hasErrors()) {
            return "departamento-form";
        }
        try {
            service.actualizar(id, departamento);
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            return "departamento-form";
        } catch (RecursoNoEncontradoException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/departamentos";
        }
        ra.addFlashAttribute("mensaje", "Departamento actualizado correctamente");
        return "redirect:/departamentos";
    }

    @PostMapping("/departamentos/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            service.eliminar(id);
            ra.addFlashAttribute("mensaje", "Departamento eliminado correctamente");
        } catch (ReglaNegocioException | RecursoNoEncontradoException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/departamentos";
    }
}