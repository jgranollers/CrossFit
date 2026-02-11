package com.example.crudthymeilif.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/concursants")
public class ConcursantController {

    @GetMapping
    public String listaConcursants(Model model) {
        return "concursants/lista";
    }

    @GetMapping("/{id}")
    public String detalleConcursant(@PathVariable Long id, Model model) {
        return "concursants/detalle";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        return "concursants/formulari";
    }

    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model) {
        return "concursants/formulari";
    }

    @PostMapping
    public String guardarConcursant() {
        return "redirect:/concursants";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarConcursant(@PathVariable Long id) {
        return "redirect:/concursants";
    }
}
