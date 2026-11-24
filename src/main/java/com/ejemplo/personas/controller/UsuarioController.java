package com.ejemplo.personas.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ejemplo.personas.model.Usuario;
import com.ejemplo.personas.service.UsuarioService;

import jakarta.validation.Valid;

@Controller
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping("/usuarios")
    public String listar(Model model) {
        model.addAttribute("usuarios", service.listar());
        return "usuarios";
    }

    @GetMapping("/usuarios/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario-form";
    }

    @PostMapping("/usuarios/guardar")
    public String guardar(@Valid @ModelAttribute Usuario usuario,
                          BindingResult resultado,
                          Model model, RedirectAttributes ra) {
        if (resultado.hasErrors()) {
            return "usuario-form";
        }
        try {
            usuario.setId(null);
            service.crear(usuario);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "usuario-form";
        }
        ra.addFlashAttribute("mensaje", "Usuario creado correctamente");
        return "redirect:/usuarios";
    }

    @GetMapping("/usuarios/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("usuario", service.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuarios";
        }
        return "usuario-form";
    }

    @PostMapping("/usuarios/{id}/actualizar")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute Usuario usuario,
                             BindingResult resultado,
                             Model model, RedirectAttributes ra) {
        usuario.setId(id);
        if (resultado.hasErrors()) {
            return "usuario-form";
        }
        try {
            service.actualizar(id, usuario);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "usuario-form";
        }
        ra.addFlashAttribute("mensaje", "Usuario actualizado correctamente");
        return "redirect:/usuarios";
    }

    @PostMapping("/usuarios/{id}/eliminar")
    public String eliminar(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
        try {
            Usuario u = service.buscarPorId(id);
            if (u.getUsername().equals(principal.getName())) {
                ra.addFlashAttribute("error", "No se puede eliminar la cuenta con la que inició sesión");
            } else {
                service.eliminar(id);
                ra.addFlashAttribute("mensaje", "Usuario eliminado correctamente");
            }
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuarios";
    }
}   