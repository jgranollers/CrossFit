package com.example.crudthymeilif.Controller;

import com.example.crudthymeilif.Model.Prestec;
import com.example.crudthymeilif.repository.LlibreRepository;
import com.example.crudthymeilif.repository.PrestecRepository;
import com.example.crudthymeilif.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/prestecs")
public class PrestecController {

    @Autowired
    private PrestecRepository prestecRepository;

    @Autowired
    private LlibreRepository llibreRepository;

    @Autowired
    private UsuariRepository usuariRepository;

    @GetMapping
    public String mostrarPrestecs(Model model) {
        List<Prestec> prestecs = prestecRepository.findAll();
        model.addAttribute("prestecs", prestecs);
        return "prestecs/llista";
    }

    @GetMapping("/nou")
    public String mostrarFormulariNou(Model model) {
        model.addAttribute("prestec", new Prestec());
        model.addAttribute("llibres", llibreRepository.findAll());
        model.addAttribute("usuaris", usuariRepository.findAll());
        return "prestecs/formulari";
    }

    @PostMapping("/desar")
    public String desarPrestec(@ModelAttribute Prestec prestec) {
        prestecRepository.save(prestec);
        return "redirect:/prestecs";
    }

    @GetMapping("/{id}")
    public String mostrarDetall(@PathVariable Integer id, Model model) {
        Optional<Prestec> prestec = prestecRepository.findById(id);
        if (prestec.isPresent()) {
            model.addAttribute("prestec", prestec.get());
            return "prestecs/detall";
        }
        return "redirect:/prestecs";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormulariEditar(@PathVariable Integer id, Model model) {
        Optional<Prestec> prestec = prestecRepository.findById(id);
        if (prestec.isPresent()) {
            model.addAttribute("prestec", prestec.get());
            model.addAttribute("llibres", llibreRepository.findAll());
            model.addAttribute("usuaris", usuariRepository.findAll());
            return "prestecs/formulari";
        }
        return "redirect:/prestecs";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPrestec(@PathVariable Integer id) {
        prestecRepository.deleteById(id);
        return "redirect:/prestecs";
    }

    @GetMapping("/api")
    @ResponseBody
    public List<Prestec> obtenirTotsPrestecs() {
        return prestecRepository.findAll();
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Prestec> obtenirPrestec(@PathVariable Integer id) {
        Optional<Prestec> prestec = prestecRepository.findById(id);
        return prestec.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/api")
    @ResponseBody
    public Prestec crearPrestec(@RequestBody Prestec prestec) {
        return prestecRepository.save(prestec);
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Prestec> actualitzarPrestec(@PathVariable Integer id, @RequestBody Prestec prestecActualitzat) {
        Optional<Prestec> prestecExistent = prestecRepository.findById(id);
        if (prestecExistent.isPresent()) {
            prestecActualitzat.setId(id);
            return ResponseEntity.ok(prestecRepository.save(prestecActualitzat));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> eliminarPrestecApi(@PathVariable Integer id) {
        if (prestecRepository.existsById(id)) {
            prestecRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
