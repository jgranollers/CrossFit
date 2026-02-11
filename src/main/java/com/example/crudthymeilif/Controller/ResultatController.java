package com.example.crudthymeilif.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/resultats")
public class ResultatController {

    @GetMapping
    public String listaResultats(Model model) {
        return "resultats/lista";
    }

    @GetMapping("/{id}")
    public String detalleResultat(@PathVariable Long id, Model model) {
        return "resultats/detalle";
    }
}
