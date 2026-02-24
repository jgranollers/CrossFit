package com.example.crudthymeilif.Controller;

import com.example.crudthymeilif.Model.Competicion;
import com.example.crudthymeilif.Model.Compra;
import com.example.crudthymeilif.Model.Concursant;
import com.example.crudthymeilif.Model.Usuari;
import com.example.crudthymeilif.Service.ConcursantService;
import com.example.crudthymeilif.Service.StripeService;
import com.example.crudthymeilif.repository.CompeticionRepository;
import com.example.crudthymeilif.repository.CompraRepository;
import com.example.crudthymeilif.repository.UsuariRepository;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @Autowired
    private StripeService stripeService;

    @Value("${stripe.public.key}")
    private String stripePublicKey;

    /**
     * POST /competiciones/{id}/inscripcio/iniciar
     * Inicia el procés d'inscripció. Si la competició té preu, redirigeix a Stripe.
     * Si és gratuïta, crea la compra directament.
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

        // Verificar que no estigui ja inscrit (COMPLETAT)
        Optional<Compra> existingCompra = compraRepository.findByCompeticioAndConcursant(competicio, concursant);
        if (existingCompra.isPresent()) {
            Compra compraExistent = existingCompra.get();
            if ("COMPLETAT".equals(compraExistent.getEstat())) {
                redirectAttributes.addFlashAttribute("error", "Ja estàs inscrit a aquesta competició.");
                return "redirect:/competiciones/" + competicioId;
            }
            // Si PENDENT: el pagament va quedar a mitges, reprendre'l
            if ("PENDENT".equals(compraExistent.getEstat()) && competicio.getPreuInscripcio() != null && competicio.getPreuInscripcio() > 0) {
                try {
                    String checkoutUrl = stripeService.crearSessioCheckout(
                            competicioId,
                            competicio.getNom(),
                            competicio.getPreuInscripcio(),
                            usuari.getCorreu()
                    );
                    return "redirect:" + checkoutUrl;
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("error", "Error al processar el pagament: " + e.getMessage());
                    return "redirect:/competiciones/" + competicioId;
                }
            }
        }

        // Verificar places disponibles
        long numInscritos = compraRepository.countByCompeticioAndEstat(competicio, "COMPLETAT");
        if (competicio.getMaxParticipants() != null && numInscritos >= competicio.getMaxParticipants()) {
            redirectAttributes.addFlashAttribute("error", "No queden places disponibles.");
            return "redirect:/competiciones/" + competicioId;
        }

        // Si la competició és gratuïta, inscriure directament
        if (competicio.getPreuInscripcio() == null || competicio.getPreuInscripcio() <= 0) {
            Compra compra = new Compra();
            compra.setUsuari(usuari);
            compra.setCompeticio(competicio);
            compra.setConcursant(concursant);
            compra.setDataCompra(LocalDateTime.now());
            compra.setPreuPagat(0.0);
            compra.setEstat("COMPLETAT");
            compra.setStripePaymentId("FREE");
            compraRepository.save(compra);

            redirectAttributes.addFlashAttribute("success", "T'has inscrit correctament a la competició!");
            return "redirect:/competiciones/" + competicioId;
        }

        // Si té preu, crear sessió de Stripe Checkout
        try {
            // Crear compra pendent
            Compra compra = new Compra();
            compra.setUsuari(usuari);
            compra.setCompeticio(competicio);
            compra.setConcursant(concursant);
            compra.setDataCompra(LocalDateTime.now());
            compra.setPreuPagat(competicio.getPreuInscripcio());
            compra.setEstat("PENDENT");
            compraRepository.save(compra);

            String checkoutUrl = stripeService.crearSessioCheckout(
                    competicioId,
                    competicio.getNom(),
                    competicio.getPreuInscripcio(),
                    usuari.getCorreu()
            );

            return "redirect:" + checkoutUrl;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al processar el pagament: " + e.getMessage());
            return "redirect:/competiciones/" + competicioId;
        }
    }

    /**
     * GET /competiciones/{id}/inscripcio/exit?session_id=...
     * Callback de Stripe quan el pagament s'ha completat correctament.
     */
    @GetMapping("/exit")
    public String inscripcioExit(@PathVariable Long competicioId,
                                  @RequestParam("session_id") String sessionId,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        try {
            Session session = stripeService.obtenirSessio(sessionId);
            String paymentStatus = session.getPaymentStatus();

            if ("paid".equals(paymentStatus)) {
                // Buscar la compra pendent de l'usuari per a aquesta competició
                String correu = authentication.getName();
                Optional<Usuari> optUsuari = usuariRepository.findByCorreu(correu);

                if (optUsuari.isPresent()) {
                    Usuari usuari = optUsuari.get();
                    Optional<Concursant> optConcursant = concursantService.obtenerConcursantDelUsuari(usuari.getDni());

                    if (optConcursant.isPresent()) {
                        Optional<Competicion> optCompeticio = competicionRepository.findById(competicioId);
                        if (optCompeticio.isPresent()) {
                            Optional<Compra> optCompra = compraRepository.findByCompeticioAndConcursant(
                                    optCompeticio.get(), optConcursant.get());

                            if (optCompra.isPresent()) {
                                Compra compra = optCompra.get();
                                compra.setEstat("COMPLETAT");
                                compra.setStripePaymentId(session.getPaymentIntent());
                                compraRepository.save(compra);
                            }
                        }
                    }
                }

                model.addAttribute("competicioId", competicioId);
                model.addAttribute("sessionId", sessionId);
                model.addAttribute("success", true);
                return "competiciones/inscripcio-exit";
            } else {
                redirectAttributes.addFlashAttribute("error", "El pagament no s'ha completat.");
                return "redirect:/competiciones/" + competicioId;
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al verificar el pagament: " + e.getMessage());
            return "redirect:/competiciones/" + competicioId;
        }
    }

    /**
     * GET /competiciones/{id}/inscripcio/cancelat
     * Callback de Stripe quan l'usuari cancel·la el pagament.
     */
    @GetMapping("/cancelat")
    public String inscripcioCancelat(@PathVariable Long competicioId,
                                      Authentication authentication,
                                      RedirectAttributes redirectAttributes) {
        // Eliminar la compra pendent si existeix
        String correu = authentication.getName();
        Optional<Usuari> optUsuari = usuariRepository.findByCorreu(correu);

        if (optUsuari.isPresent()) {
            Usuari usuari = optUsuari.get();
            Optional<Concursant> optConcursant = concursantService.obtenerConcursantDelUsuari(usuari.getDni());

            if (optConcursant.isPresent()) {
                Optional<Competicion> optCompeticio = competicionRepository.findById(competicioId);
                if (optCompeticio.isPresent()) {
                    Optional<Compra> optCompra = compraRepository.findByCompeticioAndConcursant(
                            optCompeticio.get(), optConcursant.get());

                    if (optCompra.isPresent() && "PENDENT".equals(optCompra.get().getEstat())) {
                        compraRepository.delete(optCompra.get());
                    }
                }
            }
        }

        redirectAttributes.addFlashAttribute("error", "Has cancel·lat el pagament. La inscripció no s'ha completat.");
        return "redirect:/competiciones/" + competicioId;
    }
}
