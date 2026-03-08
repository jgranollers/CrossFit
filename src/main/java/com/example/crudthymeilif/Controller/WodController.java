package com.example.crudthymeilif.Controller;

import com.example.crudthymeilif.Model.Competicion;
import com.example.crudthymeilif.Model.DificultatWod;
import com.example.crudthymeilif.Model.Exercici;
import com.example.crudthymeilif.Model.Wod;
import com.example.crudthymeilif.repository.CompeticionRepository;
import com.example.crudthymeilif.repository.DificultatWodRepository;
import com.example.crudthymeilif.repository.ExerciciRepository;
import com.example.crudthymeilif.repository.WodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/wods")
public class WodController {

    @Autowired
    private WodRepository wodRepository;

    @Autowired
    private DificultatWodRepository dificultatWodRepository;

    @Autowired
    private ExerciciRepository exerciciRepository;

    @Autowired
    private CompeticionRepository competicionRepository;

    // =============================================
    // WODS
    // =============================================

    /** Llista de WODs d'una competició */
    @GetMapping("/competicio/{compId}")
    public String listaWods(@PathVariable Long compId, Model model) {
        Optional<Competicion> optComp = competicionRepository.findById(compId);
        if (optComp.isEmpty()) return "redirect:/competiciones";

        Competicion competicion = optComp.get();
        List<Wod> wods = wodRepository.findByCompeticionOrderByOrdreAsc(competicion);

        model.addAttribute("competicion", competicion);
        model.addAttribute("wods", wods);
        return "wods/lista";
    }

    /** Formulari nou WOD */
    @GetMapping("/nou/{compId}")
    public String formulariNouWod(@PathVariable Long compId, Model model) {
        Optional<Competicion> optComp = competicionRepository.findById(compId);
        if (optComp.isEmpty()) return "redirect:/competiciones";

        Wod wod = new Wod();
        model.addAttribute("wod", wod);
        model.addAttribute("competicion", optComp.get());
        return "wods/formulari";
    }

    /** Guardar nou WOD */
    @PostMapping("/nou/{compId}")
    public String guardarNouWod(@PathVariable Long compId,
                                 @ModelAttribute Wod wod,
                                 RedirectAttributes redirectAttributes) {
        Optional<Competicion> optComp = competicionRepository.findById(compId);
        if (optComp.isEmpty()) return "redirect:/competiciones";

        Competicion competicion = optComp.get();
        wod.setCompeticion(competicion);

        // Auto-assignar ordre si no s'ha especificat
        if (wod.getOrdre() == null) {
            long count = wodRepository.countByCompeticion(competicion);
            wod.setOrdre((int) count + 1);
        }

        // Netejar subtipus si és individual
        if ("INDIVIDUAL".equals(wod.getModalitat())) {
            wod.setSubtipusGrup(null);
        }

        // Default tipus si no s'ha especificat
        if (wod.getTipus() == null || wod.getTipus().isEmpty()) {
            wod.setTipus("TIME");
        }

        wodRepository.save(wod);
        redirectAttributes.addFlashAttribute("missatge", "WOD creat correctament!");
        return "redirect:/wods/" + wod.getId();
    }

    /** Detall WOD */
    @GetMapping("/{id}")
    public String detalleWod(@PathVariable Long id, Model model) {
        Optional<Wod> optWod = wodRepository.findById(id);
        if (optWod.isEmpty()) return "redirect:/competiciones";

        Wod wod = optWod.get();
        List<DificultatWod> dificultats = dificultatWodRepository.findByWodOrderByDificultatAsc(wod);

        // Carregar exercicis per cada dificultat
        for (DificultatWod dif : dificultats) {
            List<Exercici> exercicis = exerciciRepository.findByDificultatWodOrderByOrdreAsc(dif);
            dif.setExercicis(exercicis);
        }

        // Formularis buits per afegir
        DificultatWod novaDificultat = new DificultatWod();
        Exercici nouExercici = new Exercici();

        model.addAttribute("wod", wod);
        model.addAttribute("competicion", wod.getCompeticion());
        model.addAttribute("dificultats", dificultats);
        model.addAttribute("novaDificultat", novaDificultat);
        model.addAttribute("nouExercici", nouExercici);
        return "wods/detalle";
    }

    /** Formulari editar WOD */
    @GetMapping("/{id}/editar")
    public String formulariEditarWod(@PathVariable Long id, Model model) {
        Optional<Wod> optWod = wodRepository.findById(id);
        if (optWod.isEmpty()) return "redirect:/competiciones";

        Wod wod = optWod.get();
        model.addAttribute("wod", wod);
        model.addAttribute("competicion", wod.getCompeticion());
        return "wods/formulari";
    }

    /** Guardar edició WOD */
    @PostMapping("/{id}/editar")
    public String guardarEditarWod(@PathVariable Long id,
                                    @ModelAttribute Wod wodForm,
                                    RedirectAttributes redirectAttributes) {
        Optional<Wod> optWod = wodRepository.findById(id);
        if (optWod.isEmpty()) return "redirect:/competiciones";

        Wod wod = optWod.get();
        wod.setNom(wodForm.getNom());
        wod.setModalitat(wodForm.getModalitat());
        wod.setOrdre(wodForm.getOrdre());
        wod.setTipus(wodForm.getTipus());
        wod.setEtiqueta(wodForm.getEtiqueta());

        if ("INDIVIDUAL".equals(wodForm.getModalitat())) {
            wod.setSubtipusGrup(null);
        } else {
            wod.setSubtipusGrup(wodForm.getSubtipusGrup());
        }

        wodRepository.save(wod);
        redirectAttributes.addFlashAttribute("missatge", "WOD actualitzat correctament!");
        return "redirect:/wods/" + id;
    }

    /** Eliminar WOD */
    @PostMapping("/{id}/eliminar")
    public String eliminarWod(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Wod> optWod = wodRepository.findById(id);
        if (optWod.isEmpty()) return "redirect:/competiciones";

        Long compId = optWod.get().getCompeticion().getId();
        wodRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("missatge", "WOD eliminat correctament.");
        return "redirect:/wods/competicio/" + compId;
    }

    // =============================================
    // DIFICULTATS
    // =============================================

    /** Afegir dificultat a un WOD */
    @PostMapping("/{wodId}/dificultats/afegir")
    public String afegirDificultat(@PathVariable Long wodId,
                                    @ModelAttribute DificultatWod dificultat,
                                    RedirectAttributes redirectAttributes) {
        Optional<Wod> optWod = wodRepository.findById(wodId);
        if (optWod.isEmpty()) return "redirect:/competiciones";

        dificultat.setWod(optWod.get());
        dificultatWodRepository.save(dificultat);
        redirectAttributes.addFlashAttribute("missatge", "Dificultat afegida correctament!");
        return "redirect:/wods/" + wodId;
    }

    /** Eliminar dificultat */
    @PostMapping("/{wodId}/dificultats/{difId}/eliminar")
    public String eliminarDificultat(@PathVariable Long wodId,
                                      @PathVariable Long difId,
                                      RedirectAttributes redirectAttributes) {
        dificultatWodRepository.deleteById(difId);
        redirectAttributes.addFlashAttribute("missatge", "Dificultat eliminada.");
        return "redirect:/wods/" + wodId;
    }

    // =============================================
    // EXERCICIS
    // =============================================

    /** Afegir exercici a una dificultat */
    @PostMapping("/{wodId}/dificultats/{difId}/exercicis/afegir")
    public String afegirExercici(@PathVariable Long wodId,
                                  @PathVariable Long difId,
                                  @ModelAttribute Exercici exercici,
                                  RedirectAttributes redirectAttributes) {
        Optional<DificultatWod> optDif = dificultatWodRepository.findById(difId);
        if (optDif.isEmpty()) return "redirect:/wods/" + wodId;

        DificultatWod dificultat = optDif.get();

        // Auto-assignar ordre
        if (exercici.getOrdre() == null) {
            int count = dificultat.getExercicis().size();
            exercici.setOrdre(count + 1);
        }

        exercici.setDificultatWod(dificultat);
        exerciciRepository.save(exercici);
        redirectAttributes.addFlashAttribute("missatge", "Exercici afegit correctament!");
        return "redirect:/wods/" + wodId;
    }

    /** Eliminar exercici */
    @PostMapping("/{wodId}/dificultats/{difId}/exercicis/{exId}/eliminar")
    public String eliminarExercici(@PathVariable Long wodId,
                                    @PathVariable Long difId,
                                    @PathVariable Long exId,
                                    RedirectAttributes redirectAttributes) {
        exerciciRepository.deleteById(exId);
        redirectAttributes.addFlashAttribute("missatge", "Exercici eliminat.");
        return "redirect:/wods/" + wodId;
    }
}
