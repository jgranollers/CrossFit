package com.example.crudthymeilif.Controller;

import com.example.crudthymeilif.Model.Compra;
import com.example.crudthymeilif.Model.Concursant;
import com.example.crudthymeilif.Model.Usuari;
import com.example.crudthymeilif.Service.ConcursantService;
import com.example.crudthymeilif.repository.CompraRepository;
import com.example.crudthymeilif.repository.ConcursantRepository;
import com.example.crudthymeilif.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/concursants")
public class ConcursantController {

    @Autowired
    private ConcursantRepository concursantRepository;

    @Autowired
    private UsuariRepository usuariRepository;

    @Autowired
    private ConcursantService concursantService;

    @Autowired
    private CompraRepository compraRepository;

    @GetMapping
    public String listaConcursants(Model model, Authentication authentication) {
        List<Concursant> concursants = concursantRepository.findAll();
        model.addAttribute("concursants", concursants);
        
        // Verificar si el usuario autenticado ya tiene un concursante
        boolean usuarioTieneConcursant = false;
        Concursant myConcursant = null;
        if (authentication != null && authentication.isAuthenticated()) {
            String correu = authentication.getName();
            Optional<Usuari> usuariOpt = usuariRepository.findByCorreu(correu);
            if (usuariOpt.isPresent()) {
                String usuarioDni = usuariOpt.get().getDni();
                Optional<Concursant> miConcursant = concursantService.obtenerConcursantDelUsuari(usuarioDni);
                if (miConcursant.isPresent()) {
                    usuarioTieneConcursant = true;
                    myConcursant = miConcursant.get();
                    model.addAttribute("myConcursant", myConcursant);
                }
            }
        }
        model.addAttribute("usuarioTieneConcursant", usuarioTieneConcursant);
        model.addAttribute("usuarioAutenticado", authentication != null && authentication.isAuthenticated());
        
        return "concursants/lista";
    }

    @GetMapping("/{id}")
    public String detalleConcursant(@PathVariable Long id, Model model, Authentication authentication) {
        Optional<Concursant> concursantOpt = concursantRepository.findById(id);
        if (concursantOpt.isEmpty()) {
            return "redirect:/concursants";
        }

        Concursant concursant = concursantOpt.get();
        model.addAttribute("concursant", concursant);

        List<Compra> compras = compraRepository.findAll().stream()
            .filter(c -> c.getConcursant() != null && c.getConcursant().getId().equals(id))
            .filter(c -> "COMPLETAT".equals(c.getEstat()))
            .toList();
        model.addAttribute("compras", compras);

        // Check if viewing user is owner
        boolean isMyConcursant = false;
        if (authentication != null && authentication.isAuthenticated()) {
            String correu = authentication.getName();
            Optional<Usuari> usuariOpt = usuariRepository.findByCorreu(correu);
            if (usuariOpt.isPresent() && concursant.getUsuari() != null) {
                isMyConcursant = concursant.getUsuari().getDni().equals(usuariOpt.get().getDni());
            }
        }
        model.addAttribute("isMyConcursant", isMyConcursant);

        return "concursants/detalle";
    }

    /**
     * Crear concursante personal del usuario autenticado
     */
    @PostMapping("/mi-concursant")
    public String crearMiConcursant(Authentication authentication, 
                                    @RequestParam(required = false) String categoria,
                                    @RequestParam(required = false) String sexe,
                                    @RequestParam(required = false) Integer edat,
                                    Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String correu = authentication.getName();
        Optional<Usuari> usuariOpt = usuariRepository.findByCorreu(correu);

        if (usuariOpt.isEmpty()) {
            model.addAttribute("error", "Usuario no encontrado");
            return "redirect:/concursants";
        }

        String usuarioDni = usuariOpt.get().getDni();
        // Verificar si ya tiene concursante
        if (concursantService.usuariTieneConcursant(usuarioDni)) {
            model.addAttribute("warning", "Ya tienes un concursante creado");
            return "redirect:/concursants";
        }

        // Crear concursante del usuario con los datos del formulario
        Concursant nuevo = concursantService.crearConcursantDelUsuari(usuariOpt.get(), categoria, sexe, edat);
        
        if (nuevo == null) {
            model.addAttribute("error", "Error al crear tu concursante");
            return "redirect:/concursants";
        }

        return "redirect:/concursants/" + nuevo.getId();
    }

    /**
     * DEPRECATED: No permitir creación manual de concursantes
     */
    @GetMapping("/nuevo")
    public String formularioNuevo() {
        return "redirect:/concursants";
    }

    /**
     * Formulario para crear concursante personal
     */
    @GetMapping("/nou")
    public String formularioNouConcursant(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String correu = authentication.getName();
        Optional<Usuari> usuariOpt = usuariRepository.findByCorreu(correu);

        if (usuariOpt.isEmpty()) {
            return "redirect:/concursants";
        }

        String usuarioDni = usuariOpt.get().getDni();
        // Verificar si ya tiene concursante
        if (concursantService.usuariTieneConcursant(usuarioDni)) {
            return "redirect:/concursants";
        }

        model.addAttribute("usuari", usuariOpt.get());
        return "concursants/nou";
    }

    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model, Authentication authentication) {
        Optional<Concursant> concursantOpt = concursantRepository.findById(id);
        if (concursantOpt.isEmpty()) {
            return "redirect:/concursants";
        }

        Concursant concursant = concursantOpt.get();
        model.addAttribute("concursant", concursant);

        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = false;
        if (authentication != null && authentication.isAuthenticated()) {
            String correu = authentication.getName();
            Optional<Usuari> usuariOpt = usuariRepository.findByCorreu(correu);
            if (usuariOpt.isPresent() && concursant.getUsuari() != null) {
                isOwner = concursant.getUsuari().getDni().equals(usuariOpt.get().getDni());
            }
        }

        if (!isAdmin && !isOwner) {
            return "redirect:/concursants/" + id;
        }

        return "concursants/formulari";
    }

    /**
     * SOLO permitir edición de concursante propio (o admin)
     */
    @PostMapping("/{id}/editar")
    public String guardarEdicionConcursant(@PathVariable Long id, @ModelAttribute Concursant concursant, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        String correu = authentication.getName();
        Optional<Usuari> usuariActualOpt = usuariRepository.findByCorreu(correu);
        if (usuariActualOpt.isEmpty()) return "redirect:/concursants";

        Optional<Concursant> existente = concursantRepository.findById(id);
        if (existente.isEmpty()) return "redirect:/concursants";

        Concursant c = existente.get();
        boolean isOwner = c.getUsuari() != null && c.getUsuari().getDni().equals(usuariActualOpt.get().getDni());

        if (!isAdmin && !isOwner) {
            return "redirect:/concursants";
        }

        c.setCategoria(concursant.getCategoria());
        c.setEdat(concursant.getEdat());
        c.setSexe(concursant.getSexe());
        c.setEmail(concursant.getEmail());
        c.setTelefon(concursant.getTelefon());
        if (isAdmin) {
            if (concursant.getNom() != null) c.setNom(concursant.getNom());
            if (concursant.getCognom() != null) c.setCognom(concursant.getCognom());
        }
        concursantRepository.save(c);

        return "redirect:/concursants/" + id;
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarConcursant(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        String correu = authentication.getName();
        Optional<Usuari> usuariActualOpt = usuariRepository.findByCorreu(correu);
        if (usuariActualOpt.isEmpty()) return "redirect:/concursants";

        Optional<Concursant> existente = concursantRepository.findById(id);
        if (existente.isEmpty()) return "redirect:/concursants";

        Concursant c = existente.get();
        boolean isOwner = c.getUsuari() != null && c.getUsuari().getDni().equals(usuariActualOpt.get().getDni());

        if (!isAdmin && !isOwner) {
            return "redirect:/concursants";
        }

        concursantRepository.deleteById(id);
        return "redirect:/concursants";
    }
}
