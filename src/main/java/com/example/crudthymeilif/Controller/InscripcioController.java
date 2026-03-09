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
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final Logger log = LoggerFactory.getLogger(InscripcioController.class);

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

    @PostMapping("/iniciar")
    public String iniciarInscripcio(@PathVariable Long competicioId,
            @RequestParam(defaultValue = "RX") String categoriaInscripcio,
            Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) return "redirect:/login";
        String correu = authentication.getName();
        var optUsuari = usuariRepository.findByCorreu(correu);
        if (optUsuari.isEmpty()) { redirectAttributes.addFlashAttribute("error", "Usuari no trobat."); return "redirect:/competiciones/" + competicioId; }
        var usuari = optUsuari.get();
        var optConcursant = concursantService.obtenerConcursantDelUsuari(usuari.getDni());
        if (optConcursant.isEmpty()) { redirectAttributes.addFlashAttribute("error", "Has de crear el teu perfil de concursant primer."); return "redirect:/concursants/nou"; }
        var concursant = optConcursant.get();
        var optCompeticio = competicionRepository.findById(competicioId);
        if (optCompeticio.isEmpty()) { redirectAttributes.addFlashAttribute("error", "Competicio no trobada."); return "redirect:/competiciones"; }
        var competicio = optCompeticio.get();
        if (!"OBERTA".equals(competicio.getEstat())) { redirectAttributes.addFlashAttribute("error", "La competicio no esta oberta."); return "redirect:/competiciones/" + competicioId; }
        var existingCompra = compraRepository.findByCompeticioAndConcursant(competicio, concursant);
        if (existingCompra.isPresent() && "COMPLETAT".equals(existingCompra.get().getEstat())) { redirectAttributes.addFlashAttribute("error", "Ja estas inscrit."); return "redirect:/competiciones/" + competicioId; }
        long numInscrits = compraRepository.countByCompeticioAndEstat(competicio, "COMPLETAT");
        if (competicio.getMaxParticipants() != null && numInscrits >= competicio.getMaxParticipants()) { redirectAttributes.addFlashAttribute("error", "No queden places."); return "redirect:/competiciones/" + competicioId; }
        Double preu = competicio.getPreuInscripcio();
        if (preu == null || preu == 0.0) { completarInscripcioDirecta(usuari, concursant, competicio, existingCompra, categoriaInscripcio); redirectAttributes.addFlashAttribute("success", "T'has inscrit correctament!"); return "redirect:/competiciones/" + competicioId; }
        return "redirect:/competiciones/" + competicioId + "/inscripcio/pagament?categoria=" + categoriaInscripcio;
    }

    @GetMapping("/pagament")
    public String mostrarPagament(@PathVariable Long competicioId,
            @RequestParam(required = false, defaultValue = "RX") String categoria,
            Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) return "redirect:/login";
        String correu = authentication.getName();
        var optUsuari = usuariRepository.findByCorreu(correu);
        var optCompeticio = competicionRepository.findById(competicioId);
        if (optUsuari.isEmpty() || optCompeticio.isEmpty()) return "redirect:/competiciones";
        var usuari = optUsuari.get();
        var competicio = optCompeticio.get();
        Double preu = competicio.getPreuInscripcio();
        if (preu == null || preu == 0.0) return "redirect:/competiciones/" + competicioId;
        var optConcursant = concursantService.obtenerConcursantDelUsuari(usuari.getDni());
        if (optConcursant.isEmpty()) return "redirect:/concursants/nou";
        var concursant = optConcursant.get();
        var existingCompra = compraRepository.findByCompeticioAndConcursant(competicio, concursant);
        if (existingCompra.isPresent() && "COMPLETAT".equals(existingCompra.get().getEstat())) return "redirect:/competiciones/" + competicioId;
        try {
            PaymentIntent intent = stripeService.crearPaymentIntent(competicioId, competicio.getNom(), preu, correu);
            log.info("PaymentIntent creat: {} | Competicio {}", intent.getId(), competicioId);
            if (existingCompra.isEmpty()) {
                Compra compra = new Compra();
                compra.setUsuari(usuari); compra.setCompeticio(competicio); compra.setConcursant(concursant);
                compra.setDataCompra(LocalDateTime.now()); compra.setPreuPagat(preu);
                compra.setEstat("PENDENT"); compra.setStripePaymentId(intent.getId());
                compra.setCategoriaInscripcio(categoria);
                compraRepository.save(compra);
            } else {
                existingCompra.get().setStripePaymentId(intent.getId());
                existingCompra.get().setCategoriaInscripcio(categoria);
                compraRepository.save(existingCompra.get());
            }
            model.addAttribute("stripePublicKey", stripeService.getStripePublicKey());
            model.addAttribute("clientSecret", intent.getClientSecret());
            model.addAttribute("paymentIntentId", intent.getId());
            model.addAttribute("competicio", competicio);
            model.addAttribute("competicioId", competicioId);
            model.addAttribute("preu", preu);
            model.addAttribute("categoriaInscripcio", categoria);
            return "competiciones/pagament";
        } catch (StripeException e) {
            log.error("Error PaymentIntent: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al pagament: " + e.getMessage());
            return "redirect:/competiciones/" + competicioId;
        }
    }

    @PostMapping("/pagament")
    public String processarPagament(@PathVariable Long competicioId,
            @RequestParam String paymentIntentId,
            @RequestParam String cardNumber,
            @RequestParam Long expMonth,
            @RequestParam Long expYear,
            @RequestParam String cvc,
            Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) return "redirect:/login";

        String cleanCardNumber = cardNumber.replaceAll("\\s+", "");

        try {
            PaymentIntent intent = stripeService.confirmarAmbTargeta(paymentIntentId, cleanCardNumber, expMonth, expYear, cvc);
            log.info("PaymentIntent confirmat server-side: {} | Status: {}", intent.getId(), intent.getStatus());

            if ("succeeded".equals(intent.getStatus())) {
                return "redirect:/competiciones/" + competicioId + "/inscripcio/exit?payment_intent=" + intent.getId() + "&redirect_status=succeeded";
            } else {
                redirectAttributes.addFlashAttribute("error", "El pagament no s'ha completat. Estat: " + intent.getStatus());
                return "redirect:/competiciones/" + competicioId + "/inscripcio/pagament";
            }
        } catch (StripeException e) {
            log.error("Error confirmant PaymentIntent: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al processament: " + e.getMessage());
            return "redirect:/competiciones/" + competicioId + "/inscripcio/pagament";
        }
    }

    @GetMapping("/exit")
    public String exitInscripcio(@PathVariable Long competicioId,
            @RequestParam(value = "payment_intent", required = false) String paymentIntentId,
            @RequestParam(value = "redirect_status", required = false) String redirectStatus,
            Model model, Authentication authentication) {
        if ("succeeded".equals(redirectStatus) && paymentIntentId != null && authentication != null) {
            try {
                PaymentIntent intent = stripeService.obtenirPaymentIntent(paymentIntentId);
                if ("succeeded".equals(intent.getStatus())) {
                    var optCompra = compraRepository.findByStripePaymentId(paymentIntentId);
                    if (optCompra.isPresent() && !"COMPLETAT".equals(optCompra.get().getEstat())) {
                        Compra compra = optCompra.get();
                        compra.setEstat("COMPLETAT"); compra.setDataCompra(LocalDateTime.now());
                        compraRepository.save(compra);
                    }
                }
            } catch (StripeException e) { log.error("Error verificant PaymentIntent: {}", e.getMessage()); }
        }
        model.addAttribute("paymentIntentId", paymentIntentId);
        model.addAttribute("competicioId", competicioId);
        model.addAttribute("success", "succeeded".equals(redirectStatus));
        return "competiciones/inscripcio-exit";
    }

    @GetMapping("/cancelat")
    public String cancelatInscripcio(@PathVariable Long competicioId, Model model) {
        model.addAttribute("competicioId", competicioId);
        return "competiciones/inscripcio-cancelat";
    }

    private void completarInscripcioDirecta(Usuari usuari, Concursant concursant, Competicion competicio, Optional<Compra> existingCompra, String categoriaInscripcio) {
        Compra compra = existingCompra.orElseGet(Compra::new);
        compra.setUsuari(usuari); compra.setCompeticio(competicio); compra.setConcursant(concursant);
        compra.setDataCompra(LocalDateTime.now()); compra.setPreuPagat(0.0);
        compra.setEstat("COMPLETAT"); compra.setStripePaymentId("GRATIS");
        compra.setCategoriaInscripcio(categoriaInscripcio);
        compraRepository.save(compra);
    }
}