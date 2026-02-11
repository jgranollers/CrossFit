package com.example.crudthymeilif.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/competiciones")
public class CompeticionController {

    @GetMapping
    public String listaCompeticiones(Model model) {
        return "competiciones/lista";
    }

    @GetMapping("/{id}")
    public String detalleCompeticion(@PathVariable Long id, Model model) {
        return "competiciones/detalle";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        return "competiciones/formulari";
    }

    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model) {
        return "competiciones/formulari";
    }

    @PostMapping
    public String guardarCompeticion() {
        return "redirect:/competiciones";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarCompeticion(@PathVariable Long id) {
        return "redirect:/competiciones";
    }
}
