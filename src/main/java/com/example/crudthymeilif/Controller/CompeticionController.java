package com.example.crudthymeilif.Controller;

import com.example.crudthymeilif.Model.Competicion;
import com.example.crudthymeilif.Model.Compra;
import com.example.crudthymeilif.Model.Concursant;
import com.example.crudthymeilif.Model.Usuari;
import com.example.crudthymeilif.Service.ConcursantService;
import com.example.crudthymeilif.repository.CompeticionRepository;
import com.example.crudthymeilif.repository.CompraRepository;
import com.example.crudthymeilif.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${stripe.public.key}")
    private String stripePublicKey;

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
            model.addAttribute("stripePublicKey", stripePublicKey);

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
                        boolean pendent = inscripcio.isPresent() && "PENDENT".equals(inscripcio.get().getEstat());
                        model.addAttribute("jaInscrit", completat);
                        model.addAttribute("tenguiPendent", pendent);
                        model.addAttribute("concursantActual", optConcursant.get());
                    } else {
                        model.addAttribute("jaInscrit", false);
                        model.addAttribute("tenguiPendent", false);
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
    public String guardarCompeticion(@Valid @ModelAttribute Competicion competicion, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("competicion", competicion);
            return "competiciones/formulari";
        }
        competicionRepository.save(competicion);
        return "redirect:/competiciones";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarCompeticion(@PathVariable Long id) {
        competicionRepository.deleteById(id);
        return "redirect:/competiciones";
    }
}
