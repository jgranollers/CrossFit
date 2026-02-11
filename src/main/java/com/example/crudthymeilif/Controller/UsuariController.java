package com.example.crudthymeilif.Controller;

import com.example.crudthymeilif.Model.Usuari;
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

    @GetMapping
    public String mostrarUsuaris(Model model) {
        return "usuaris/llista";
    }

    @GetMapping("/nou")
    public String mostrarFormulariNou(Model model) {
        model.addAttribute("usuari", new Usuari());
        return "usuaris/formulari";
    }

    @GetMapping("/editar/{dni}")
    public String mostrarFormulariEditar(@PathVariable String dni, Model model) {
        return "usuaris/formulari";
    }

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

    // Esta ruta DEBE ir al final porque captura cualquier valor
    @GetMapping("/{dni}")
    public String mostrarDetall(@PathVariable String dni, Model model) {
        return "usuaris/detall";
    }
}
