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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/competiciones/{competicioId}/inscripcio")
public class InscripcioController {

    @Autowired
    private CompeticionRepository competicionRepository;

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private UsuariRepository usuariRepository;

    @Autowired
    private ConcursantService concursantService;

    /**
     * POST /competiciones/{id}/inscripcio/iniciar
     * Inscripció directa sense Stripe - totes les inscripcions es completen immediatament.
     */
    @PostMapping("/iniciar")
    public String iniciarInscripcio(@PathVariable Long competicioId,
                                     Authentication authentication,
                                     RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String correu = authentication.getName();
        Optional<Usuari> optUsuari = usuariRepository.findByCorreu(correu);
        if (optUsuari.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Usuari no trobat.");
            return "redirect:/competiciones/" + competicioId;
        }

        Usuari usuari = optUsuari.get();

        // Verificar que l'usuari té un perfil de concursant
        Optional<Concursant> optConcursant = concursantService.obtenerConcursantDelUsuari(usuari.getDni());
        if (optConcursant.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Has de crear el teu perfil de concursant primer.");
            return "redirect:/concursants/nou";
        }

        Concursant concursant = optConcursant.get();

        // Verificar que la competició existeix
        Optional<Competicion> optCompeticio = competicionRepository.findById(competicioId);
        if (optCompeticio.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Competició no trobada.");
            return "redirect:/competiciones";
        }

        Competicion competicio = optCompeticio.get();

        // Verificar que la competició està oberta
        if (!"OBERTA".equals(competicio.getEstat())) {
            redirectAttributes.addFlashAttribute("error", "La competició no està oberta per a inscripcions.");
            return "redirect:/competiciones/" + competicioId;
        }

        // Verificar que no estigui ja inscrit
        Optional<Compra> existingCompra = compraRepository.findByCompeticioAndConcursant(competicio, concursant);
        if (existingCompra.isPresent() && "COMPLETAT".equals(existingCompra.get().getEstat())) {
            redirectAttributes.addFlashAttribute("error", "Ja estàs inscrit a aquesta competició.");
            return "redirect:/competiciones/" + competicioId;
        }

        // Verificar places disponibles
        long numInscritos = compraRepository.countByCompeticioAndEstat(competicio, "COMPLETAT");
        if (competicio.getMaxParticipants() != null && numInscritos >= competicio.getMaxParticipants()) {
            redirectAttributes.addFlashAttribute("error", "No queden places disponibles.");
            return "redirect:/competiciones/" + competicioId;
        }

        // Inscripció directa
        if (existingCompra.isPresent()) {
            // Si tenia una compra pendent, la completem
            Compra compra = existingCompra.get();
            compra.setEstat("COMPLETAT");
            compra.setDataCompra(LocalDateTime.now());
            compra.setPreuPagat(0.0);
            compra.setStripePaymentId("DIRECT");
            compraRepository.save(compra);
        } else {
            Compra compra = new Compra();
            compra.setUsuari(usuari);
            compra.setCompeticio(competicio);
            compra.setConcursant(concursant);
            compra.setDataCompra(LocalDateTime.now());
            compra.setPreuPagat(0.0);
            compra.setEstat("COMPLETAT");
            compra.setStripePaymentId("DIRECT");
            compraRepository.save(compra);
        }

        redirectAttributes.addFlashAttribute("success", "T'has inscrit correctament a la competició!");
        return "redirect:/competiciones/" + competicioId;
    }
}
