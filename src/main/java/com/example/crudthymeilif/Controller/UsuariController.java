package com.example.crudthymeilif.Controller;

import com.example.crudthymeilif.Model.Usuari;
import com.example.crudthymeilif.repository.ConcursantRepository;
import com.example.crudthymeilif.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/usuaris")
public class UsuariController {

    @Autowired
    private UsuariRepository usuariRepository;

    @Autowired
    private ConcursantRepository concursantRepository;

    // ── Thymeleaf views ──────────────────────────────────────────────────────

    @GetMapping
    public String mostrarUsuaris(Model model) {
        model.addAttribute("usuaris", usuariRepository.findAll());
        return "usuaris/llista";
    }

    @GetMapping("/nou")
    public String mostrarFormulariNou(Model model) {
        model.addAttribute("usuariForm", new Usuari());
        model.addAttribute("editMode", false);
        return "usuaris/formulari";
    }

    @GetMapping("/editar/{dni}")
    public String mostrarFormulariEditar(@PathVariable String dni, Model model) {
        Optional<Usuari> u = usuariRepository.findById(dni);
        if (u.isEmpty()) return "redirect:/usuaris";
        model.addAttribute("usuariForm", u.get());
        model.addAttribute("editMode", true);
        return "usuaris/formulari";
    }

    @PostMapping("/nou")
    public String crearUsuariForm(@ModelAttribute("usuariForm") Usuari usuari,
                                   @RequestParam(required = false) String rolSelect) {
        if (rolSelect != null && !rolSelect.isEmpty()) usuari.setRol(rolSelect);
        if (usuari.getRol() == null) usuari.setRol("USUARI");
        usuariRepository.save(usuari);
        return "redirect:/usuaris";
    }

    @PostMapping("/editar/{dni}")
    public String actualitzarUsuariForm(@PathVariable String dni,
                                         @ModelAttribute("usuariForm") Usuari form,
                                         @RequestParam(required = false) String rolSelect) {
        Optional<Usuari> existing = usuariRepository.findById(dni);
        if (existing.isEmpty()) return "redirect:/usuaris";
        Usuari u = existing.get();
        u.setNom(form.getNom());
        u.setTelefon(form.getTelefon());
        u.setCorreu(form.getCorreu());
        u.setNacionalitat(form.getNacionalitat());
        if (rolSelect != null && !rolSelect.isEmpty()) u.setRol(rolSelect);
        usuariRepository.save(u);
        return "redirect:/usuaris/" + dni;
    }

    @PostMapping("/{dni}/eliminar")
    public String eliminarUsuariForm(@PathVariable String dni) {
        if (usuariRepository.existsById(dni)) usuariRepository.deleteById(dni);
        return "redirect:/usuaris";
    }

    // Esta ruta DEBE ir al final porque captura cualquier valor
    @GetMapping("/{dni}")
    public String mostrarDetall(@PathVariable String dni, Model model) {
        Optional<Usuari> u = usuariRepository.findById(dni);
        if (u.isEmpty()) return "redirect:/usuaris";
        model.addAttribute("usuari", u.get());
        model.addAttribute("concursant", concursantRepository.findAll().stream()
                .filter(c -> c.getUsuari() != null && c.getUsuari().getDni().equals(dni))
                .findFirst().orElse(null));
        return "usuaris/detall";
    }

    // ── REST API (retained for compatibility) ────────────────────────────────

    @GetMapping("/api")
    @ResponseBody
    public List<Usuari> obtenirTotsUsuaris() {
        return usuariRepository.findAll();
    }

    @GetMapping("/api/{dni}")
    @ResponseBody
    public ResponseEntity<Usuari> obtenirUsuari(@PathVariable String dni) {
        Optional<Usuari> usuari = usuariRepository.findById(dni);
        return usuari.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/api")
    @ResponseBody
    public Usuari crearUsuari(@RequestBody Usuari usuari) {
        return usuariRepository.save(usuari);
    }

    @PutMapping("/api/{dni}")
    @ResponseBody
    public ResponseEntity<Usuari> actualitzarUsuari(@PathVariable String dni, @RequestBody Usuari usuariActualitzat) {
        Optional<Usuari> usuariExistent = usuariRepository.findById(dni);
        if (usuariExistent.isPresent()) {
            usuariActualitzat.setDni(dni);
            return ResponseEntity.ok(usuariRepository.save(usuariActualitzat));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/api/{dni}")
    @ResponseBody
    public ResponseEntity<Void> eliminarUsuariApi(@PathVariable String dni) {
        if (usuariRepository.existsById(dni)) {
            usuariRepository.deleteById(dni);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}

