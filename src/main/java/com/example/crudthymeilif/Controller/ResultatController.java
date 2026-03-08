package com.example.crudthymeilif.Controller;

import com.example.crudthymeilif.Model.*;
import com.example.crudthymeilif.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private WodRepository wodRepository;

    @Autowired
    private WodCompletRepository wodCompletRepository;

    @Autowired
    private PuntuacioRepository puntuacioRepository;

    /**
     * Llista de competicions amb resultats
     */
    @GetMapping
    public String listaResultats(Model model) {
        List<Competicion> competicions = competicionRepository.findAll();
        model.addAttribute("competicions", competicions);
        return "resultats/lista";
    }

    /**
     * Detall de puntuacions d'una competició: formulari + rankings amb pestanyes
     */
    @GetMapping("/competicio/{compId}")
    public String detalleResultatsCompeticio(@PathVariable Long compId, Model model) {
        Optional<Competicion> optComp = competicionRepository.findById(compId);
        if (optComp.isEmpty()) return "redirect:/resultats";

        Competicion comp = optComp.get();
        List<Wod> wods = wodRepository.findByCompeticionOrderByOrdreAsc(comp);

        // Concursants inscrits
        List<Compra> compras = compraRepository.findByCompeticio(comp).stream()
                .filter(c -> "COMPLETAT".equals(c.getEstat()))
                .collect(Collectors.toList());
        List<Concursant> concursantsInscrits = compras.stream()
                .map(Compra::getConcursant)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // Totes les puntuacions d'aquesta competició
        List<Puntuacio> puntuacions = puntuacioRepository.findByCompeticio(comp);

        // Rankings per cada WOD
        Map<Long, List<Map<String, Object>>> rankingPerWod = new LinkedHashMap<>();
        // Punts acumulats per General
        Map<Long, Integer> puntsGeneralByConcursant = new HashMap<>();
        Map<Long, Concursant> concursantMap = new HashMap<>();

        for (Wod wod : wods) {
            List<Puntuacio> puntsWod = puntuacions.stream()
                    .filter(p -> p.getWod().getId().equals(wod.getId()))
                    .collect(Collectors.toList());

            // Ordenar segons tipus
            List<Puntuacio> sorted;
            if ("TIME".equals(wod.getTipus())) {
                sorted = puntsWod.stream()
                        .sorted(Comparator.comparingInt(Puntuacio::getTempsEnSegons))
                        .collect(Collectors.toList());
            } else if ("WEIGHT".equals(wod.getTipus())) {
                sorted = puntsWod.stream()
                        .sorted(Comparator.comparingDouble((Puntuacio p) -> p.getKg() != null ? p.getKg() : 0).reversed())
                        .collect(Collectors.toList());
            } else { // REPS
                sorted = puntsWod.stream()
                        .sorted(Comparator.comparingInt((Puntuacio p) -> p.getReps() != null ? p.getReps() : 0).reversed())
                        .collect(Collectors.toList());
            }

            List<Map<String, Object>> ranking = new ArrayList<>();
            for (int i = 0; i < sorted.size(); i++) {
                Puntuacio p = sorted.get(i);
                Map<String, Object> item = new HashMap<>();
                item.put("concursant", p.getConcursant());
                item.put("puntuacio", p);
                item.put("posicio", i + 1);
                item.put("punts", i + 1); // 1r = 1 punt, 2n = 2, etc.
                ranking.add(item);

                Long cid = p.getConcursant().getId();
                puntsGeneralByConcursant.merge(cid, i + 1, Integer::sum);
                concursantMap.putIfAbsent(cid, p.getConcursant());
            }
            rankingPerWod.put(wod.getId(), ranking);
        }

        // Ranking General: menys punts = millor
        List<Map<String, Object>> rankingGeneral = puntsGeneralByConcursant.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("concursant", concursantMap.get(entry.getKey()));
                    item.put("punts", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());
        for (int i = 0; i < rankingGeneral.size(); i++) {
            rankingGeneral.get(i).put("posicio", i + 1);
        }

        model.addAttribute("competicion", comp);
        model.addAttribute("wods", wods);
        model.addAttribute("concursants", concursantsInscrits);
        model.addAttribute("rankingPerWod", rankingPerWod);
        model.addAttribute("rankingGeneral", rankingGeneral);
        return "resultats/detalle";
    }

    /**
     * POST: Guardar puntuació (nova o actualitzar)
     */
    @PostMapping("/competicio/{compId}/puntuacio")
    @Transactional
    public String guardarPuntuacio(@PathVariable Long compId,
                                    @RequestParam Long wodId,
                                    @RequestParam Long concursantId,
                                    @RequestParam(required = false) String temps,
                                    @RequestParam(required = false) Double kg,
                                    @RequestParam(required = false) Integer reps,
                                    RedirectAttributes redirectAttributes) {
        Optional<Competicion> optComp = competicionRepository.findById(compId);
        Optional<Wod> optWod = wodRepository.findById(wodId);
        Optional<Concursant> optConc = concursantRepository.findById(concursantId);

        if (optComp.isEmpty() || optWod.isEmpty() || optConc.isEmpty()) {
            return "redirect:/resultats";
        }

        Wod wod = optWod.get();
        Concursant concursant = optConc.get();
        Competicion comp = optComp.get();

        // Buscar o crear puntuació
        Puntuacio punt = puntuacioRepository.findByWodAndConcursant(wod, concursant)
                .orElse(new Puntuacio());

        punt.setWod(wod);
        punt.setConcursant(concursant);
        punt.setCompeticio(comp);

        switch (wod.getTipus()) {
            case "TIME" -> punt.setTemps(temps);
            case "WEIGHT" -> punt.setKg(kg);
            case "REPS" -> punt.setReps(reps);
        }

        puntuacioRepository.save(punt);
        redirectAttributes.addFlashAttribute("missatge", "Puntuació guardada correctament!");
        return "redirect:/resultats/competicio/" + compId;
    }

    /**
     * POST: Eliminar puntuació
     */
    @PostMapping("/puntuacio/{puntId}/eliminar")
    @Transactional
    public String eliminarPuntuacio(@PathVariable Long puntId,
                                     RedirectAttributes redirectAttributes) {
        Optional<Puntuacio> optPunt = puntuacioRepository.findById(puntId);
        if (optPunt.isEmpty()) return "redirect:/resultats";

        Long compId = optPunt.get().getCompeticio().getId();
        puntuacioRepository.deleteById(puntId);
        redirectAttributes.addFlashAttribute("missatge", "Puntuació eliminada.");
        return "redirect:/resultats/competicio/" + compId;
    }

    /**
     * Leaderboard públic d'una competició
     */
    @GetMapping("/leaderboard/{compId}")
    public String leaderboardPublic(@PathVariable Long compId, Model model) {
        Optional<Competicion> optComp = competicionRepository.findById(compId);
        if (optComp.isEmpty()) return "redirect:/resultats";

        Competicion comp = optComp.get();
        List<Wod> wods = wodRepository.findByCompeticionOrderByOrdreAsc(comp);
        List<Puntuacio> puntuacions = puntuacioRepository.findByCompeticio(comp);

        Map<Long, List<Map<String, Object>>> rankingPerWod = new LinkedHashMap<>();
        Map<Long, Integer> puntsGeneralByConcursant = new HashMap<>();
        Map<Long, Concursant> concursantMap = new HashMap<>();

        for (Wod wod : wods) {
            List<Puntuacio> puntsWod = puntuacions.stream()
                    .filter(p -> p.getWod().getId().equals(wod.getId()))
                    .collect(Collectors.toList());

            List<Puntuacio> sorted;
            if ("TIME".equals(wod.getTipus())) {
                sorted = puntsWod.stream()
                        .sorted(Comparator.comparingInt(Puntuacio::getTempsEnSegons))
                        .collect(Collectors.toList());
            } else if ("WEIGHT".equals(wod.getTipus())) {
                sorted = puntsWod.stream()
                        .sorted(Comparator.comparingDouble((Puntuacio p) -> p.getKg() != null ? p.getKg() : 0).reversed())
                        .collect(Collectors.toList());
            } else {
                sorted = puntsWod.stream()
                        .sorted(Comparator.comparingInt((Puntuacio p) -> p.getReps() != null ? p.getReps() : 0).reversed())
                        .collect(Collectors.toList());
            }

            List<Map<String, Object>> ranking = new ArrayList<>();
            for (int i = 0; i < sorted.size(); i++) {
                Puntuacio p = sorted.get(i);
                Map<String, Object> item = new HashMap<>();
                item.put("concursant", p.getConcursant());
                item.put("puntuacio", p);
                item.put("posicio", i + 1);
                item.put("punts", i + 1);
                ranking.add(item);

                Long cid = p.getConcursant().getId();
                puntsGeneralByConcursant.merge(cid, i + 1, Integer::sum);
                concursantMap.putIfAbsent(cid, p.getConcursant());
            }
            rankingPerWod.put(wod.getId(), ranking);
        }

        List<Map<String, Object>> rankingGeneral = puntsGeneralByConcursant.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("concursant", concursantMap.get(entry.getKey()));
                    item.put("punts", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());
        for (int i = 0; i < rankingGeneral.size(); i++) {
            rankingGeneral.get(i).put("posicio", i + 1);
        }

        model.addAttribute("competicion", comp);
        model.addAttribute("wods", wods);
        model.addAttribute("rankingPerWod", rankingPerWod);
        model.addAttribute("rankingGeneral", rankingGeneral);
        return "resultats/leaderboard";
    }

    // Compatibilitat antiga - redirigir
    @PostMapping("/wod/{wodId}/concursant/{concursantId}/completar")
    @Transactional
    public String marcarWodCompletat(@PathVariable Long wodId,
                                      @PathVariable Long concursantId,
                                      RedirectAttributes redirectAttributes) {
        Optional<Wod> optWod = wodRepository.findById(wodId);
        if (optWod.isEmpty()) return "redirect:/resultats";
        return "redirect:/resultats/competicio/" + optWod.get().getCompeticion().getId();
    }

    @PostMapping("/wod/{wodId}/concursant/{concursantId}/descompletar")
    @Transactional
    public String desmarcarWodCompletat(@PathVariable Long wodId,
                                         @PathVariable Long concursantId,
                                         RedirectAttributes redirectAttributes) {
        Optional<Wod> optWod = wodRepository.findById(wodId);
        if (optWod.isEmpty()) return "redirect:/resultats";
        return "redirect:/resultats/competicio/" + optWod.get().getCompeticion().getId();
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarResultat(@PathVariable Long id) {
        resultatRepository.deleteById(id);
        return "redirect:/resultats";
    }
}
