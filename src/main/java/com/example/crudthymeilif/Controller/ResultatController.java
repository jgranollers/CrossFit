package com.example.crudthymeilif.Controller;

import com.example.crudthymeilif.Model.Competicion;
import com.example.crudthymeilif.Model.Compra;
import com.example.crudthymeilif.Model.Concursant;
import com.example.crudthymeilif.Model.Resultat;
import com.example.crudthymeilif.repository.CompeticionRepository;
import com.example.crudthymeilif.repository.CompraRepository;
import com.example.crudthymeilif.repository.ConcursantRepository;
import com.example.crudthymeilif.repository.ResultatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/resultats")
public class ResultatController {

    @Autowired
    private ResultatRepository resultatRepository;

    @Autowired
    private CompeticionRepository competicionRepository;

    @Autowired
    private ConcursantRepository concursantRepository;

    @Autowired
    private CompraRepository compraRepository;

    @GetMapping
    public String listaResultats(Model model) {
        List<Competicion> competicions = competicionRepository.findAll();

        // Build map: competicioId -> top-3 resultats ordered by posicio
        Map<Long, List<Resultat>> resultatsByComp = new LinkedHashMap<>();
        for (Competicion comp : competicions) {
            List<Resultat> rs = resultatRepository.findByCompeticioOrderByPosicioAsc(comp)
                    .stream().limit(3).collect(Collectors.toList());
            resultatsByComp.put(comp.getId(), rs);
        }

        model.addAttribute("competicions", competicions);
        model.addAttribute("resultatsByComp", resultatsByComp);
        return "resultats/lista";
    }

    @GetMapping("/nou")
    public String nouResultatForm(Model model) {
        List<Competicion> competicions = competicionRepository.findAll();
        List<Compra> compras = compraRepository.findAll().stream()
                .filter(c -> "COMPLETAT".equals(c.getEstat()))
                .collect(Collectors.toList());
        model.addAttribute("competicions", competicions);
        model.addAttribute("compras", compras);
        return "resultats/formulari";
    }

    @PostMapping
    public String guardarResultats(
            @RequestParam Long competicioId,
            @RequestParam(required = false) Long primerConcursantId,
            @RequestParam(required = false) Long segonConcursantId,
            @RequestParam(required = false) Long tercerConcursantId) {

        Optional<Competicion> compOpt = competicionRepository.findById(competicioId);
        if (compOpt.isEmpty()) return "redirect:/resultats";
        Competicion comp = compOpt.get();

        // Delete existing results for this competition
        List<Resultat> existents = resultatRepository.findByCompeticio(comp);
        resultatRepository.deleteAll(existents);

        // Save new positions
        savePosition(comp, primerConcursantId, 1);
        savePosition(comp, segonConcursantId, 2);
        savePosition(comp, tercerConcursantId, 3);

        return "redirect:/resultats";
    }

    private void savePosition(Competicion comp, Long concursantId, int posicio) {
        if (concursantId == null) return;
        concursantRepository.findById(concursantId).ifPresent(c -> {
            Resultat r = new Resultat();
            r.setCompeticio(comp);
            r.setConcursant(c);
            r.setPosicio(posicio);
            resultatRepository.save(r);
        });
    }

    @GetMapping("/{id}")
    public String detalleResultat(@PathVariable Long id, Model model) {
        resultatRepository.findById(id).ifPresent(resultat ->
            model.addAttribute("resultat", resultat)
        );
        return "resultats/detalle";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarResultat(@PathVariable Long id) {
        resultatRepository.deleteById(id);
        return "redirect:/resultats";
    }
}

