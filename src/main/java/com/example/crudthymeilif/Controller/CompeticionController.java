package com.example.crudthymeilif.Controller;

import com.example.crudthymeilif.Model.Competicion;
import com.example.crudthymeilif.Model.Compra;
import com.example.crudthymeilif.Model.Concursant;
import com.example.crudthymeilif.Model.Usuari;
import com.example.crudthymeilif.Service.ConcursantService;
import com.example.crudthymeilif.repository.CompeticionRepository;
import com.example.crudthymeilif.repository.CompraRepository;
import com.example.crudthymeilif.repository.PuntuacioRepository;
import com.example.crudthymeilif.repository.ResultatRepository;
import com.example.crudthymeilif.repository.UsuariRepository;
import com.example.crudthymeilif.repository.WodCompletRepository;
import com.example.crudthymeilif.repository.WodRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/competiciones")
public class CompeticionController {

    @Autowired
    private CompeticionRepository competicionRepository;

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private UsuariRepository usuariRepository;

    @Autowired
    private ConcursantService concursantService;

    @Autowired
    private WodRepository wodRepository;

    @Autowired
    private PuntuacioRepository puntuacioRepository;

    @Autowired
    private ResultatRepository resultatRepository;

    @Autowired
    private WodCompletRepository wodCompletRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping
    public String listaCompeticiones(Model model) {
        List<Competicion> competiciones = competicionRepository.findAll();
        Map<Long, Integer> porcentajesOcupacion = new HashMap<>();
        Map<Long, Long> inscritos = new HashMap<>();
        
        for (Competicion comp : competiciones) {
            long numInscritos = compraRepository.countByCompeticioAndEstat(comp, "COMPLETAT");
            inscritos.put(comp.getId(), numInscritos);
            
            if (comp.getMaxParticipants() != null && comp.getMaxParticipants() > 0) {
                int porcentaje = (int) ((numInscritos * 100) / comp.getMaxParticipants());
                porcentajesOcupacion.put(comp.getId(), Math.min(porcentaje, 100));
            } else {
                porcentajesOcupacion.put(comp.getId(), 0);
            }
        }
        
        model.addAttribute("competiciones", competiciones);
        model.addAttribute("porcentajesOcupacion", porcentajesOcupacion);
        model.addAttribute("inscritos", inscritos);
        return "competiciones/lista";
    }

    @GetMapping("/{id}")
    public String detalleCompeticion(@PathVariable Long id, Model model, Authentication authentication) {
        competicionRepository.findById(id).ifPresent(competicion -> {
            model.addAttribute("competicion", competicion);
            
            List<Compra> compras = compraRepository.findByCompeticio(competicion);
            long numInscritos = compraRepository.countByCompeticioAndEstat(competicion, "COMPLETAT");
            
            int porcentaje = 0;
            if (competicion.getMaxParticipants() != null && competicion.getMaxParticipants() > 0) {
                porcentaje = (int) ((numInscritos * 100) / competicion.getMaxParticipants());
            }
            
            model.addAttribute("compras", compras);
            model.addAttribute("numInscritos", numInscritos);
            model.addAttribute("porcentajeOcupacion", Math.min(porcentaje, 100));

            // Verificar estat de l'usuari actual
            if (authentication != null && authentication.isAuthenticated()) {
                String correu = authentication.getName();
                Optional<Usuari> optUsuari = usuariRepository.findByCorreu(correu);
                if (optUsuari.isPresent()) {
                    Usuari usuari = optUsuari.get();
                    Optional<Concursant> optConcursant = concursantService.obtenerConcursantDelUsuari(usuari.getDni());
                    
                    model.addAttribute("teConcursant", optConcursant.isPresent());
                    
                    if (optConcursant.isPresent()) {
                        Optional<Compra> inscripcio = compraRepository.findByCompeticioAndConcursant(competicion, optConcursant.get());
                        boolean completat = inscripcio.isPresent() && "COMPLETAT".equals(inscripcio.get().getEstat());
                        model.addAttribute("jaInscrit", completat);
                        model.addAttribute("concursantActual", optConcursant.get());
                    } else {
                        model.addAttribute("jaInscrit", false);
                    }
                } else {
                    model.addAttribute("teConcursant", false);
                    model.addAttribute("jaInscrit", false);
                }
            }
        });
        return "competiciones/detalle";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("competicion", new Competicion());
        return "competiciones/formulari";
    }

    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model) {
        competicionRepository.findById(id).ifPresent(competicion -> 
            model.addAttribute("competicion", competicion)
        );
        return "competiciones/formulari";
    }

    @PostMapping
    @Transactional
    public String guardarCompeticion(@Valid @ModelAttribute Competicion competicionForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("competicion", competicionForm);
            return "competiciones/formulari";
        }
        if (competicionForm.getId() != null) {
            // EDIT: load existing entity to avoid orphanRemoval deleting all wods
            Optional<Competicion> opt = competicionRepository.findById(competicionForm.getId());
            if (opt.isPresent()) {
                Competicion existing = opt.get();
                existing.setNom(competicionForm.getNom());
                existing.setTipusCompeticio(competicionForm.getTipusCompeticio());
                existing.setDataCompeticio(competicionForm.getDataCompeticio());
                existing.setLocalitat(competicionForm.getLocalitat());
                existing.setDescripcio(competicionForm.getDescripcio());
                existing.setPreuInscripcio(competicionForm.getPreuInscripcio());
                existing.setMaxParticipants(competicionForm.getMaxParticipants());
                existing.setEstat(competicionForm.getEstat());
                competicionRepository.save(existing);
                return "redirect:/competiciones";
            }
        }
        competicionRepository.save(competicionForm);
        return "redirect:/competiciones";
    }

    @PostMapping("/{id}/eliminar")
    @Transactional
    public String eliminarCompeticion(@PathVariable Long id) {
        if (!competicionRepository.existsById(id)) return "redirect:/competiciones";

        // Disable FK checks to handle all cascading dependencies cleanly
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
        try {
            entityManager.createNativeQuery("DELETE FROM puntuacio WHERE competicio_id = :id").setParameter("id", id).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM wod_complet WHERE wod_id IN (SELECT id FROM wod WHERE competicion_id = :id)").setParameter("id", id).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM resultat WHERE competicio_id = :id").setParameter("id", id).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM compra WHERE competicio_id = :id").setParameter("id", id).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM exercici WHERE dificultat_wod_id IN (SELECT d.id FROM dificultat_wod d JOIN wod w ON d.wod_id = w.id WHERE w.competicion_id = :id)").setParameter("id", id).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM dificultat_wod WHERE wod_id IN (SELECT id FROM wod WHERE competicion_id = :id)").setParameter("id", id).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM equip WHERE competicio_id = :id").setParameter("id", id).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM wod WHERE competicion_id = :id").setParameter("id", id).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM competicion WHERE id = :id").setParameter("id", id).executeUpdate();
        } finally {
            entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
        }

        return "redirect:/competiciones";
    }
}
