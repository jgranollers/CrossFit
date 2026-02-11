package com.example.crudthymeilif.Controller;

import com.example.crudthymeilif.Model.Llibre;
import com.example.crudthymeilif.repository.LlibreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/llibres")
public class LlibreController {

    @Autowired
    private LlibreRepository llibreRepository;

    @GetMapping
    public String mostrarLlibres(Model model) {
        return "llibres/llista";
    }

    @GetMapping("/nou")
    public String mostrarFormulariNou(Model model) {
        model.addAttribute("llibre", new Llibre());
        return "llibres/formulari";
    }

    @GetMapping("/editar/{isbn}")
    public String mostrarFormulariEditar(@PathVariable String isbn, Model model) {
        return "llibres/formulari";
    }

    @GetMapping("/api")
    @ResponseBody
    public List<Llibre> obtenirTotsLlibres() {
        return llibreRepository.findAll();
    }

    @GetMapping("/api/{isbn}")
    @ResponseBody
    public ResponseEntity<Llibre> obtenirLlibre(@PathVariable String isbn) {
        Optional<Llibre> llibre = llibreRepository.findById(isbn);
        return llibre.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<Llibre> crearLlibre(@RequestBody Llibre llibre) {
        Llibre nouLlibre = llibreRepository.save(llibre);
        return ResponseEntity.ok(nouLlibre);
    }

    @PutMapping("/api/{isbn}")
    @ResponseBody
    public ResponseEntity<Llibre> actualitzarLlibre(@PathVariable String isbn, @RequestBody Llibre llibreActualitzat) {
        Optional<Llibre> llibreExistent = llibreRepository.findById(isbn);
        if (llibreExistent.isPresent()) {
            llibreActualitzat.setIsbn(isbn);
            Llibre llibreDesat = llibreRepository.save(llibreActualitzat);
            return ResponseEntity.ok(llibreDesat);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/api/{isbn}")
    @ResponseBody
    public ResponseEntity<Void> eliminarLlibre(@PathVariable String isbn) {
        if (llibreRepository.existsById(isbn)) {
            llibreRepository.deleteById(isbn);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{isbn}")
    public String mostrarDetall(@PathVariable String isbn, Model model) {
        return "llibres/detall";
    }
}
