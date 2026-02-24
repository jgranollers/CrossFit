package com.example.crudthymeilif.Controller;

import com.example.crudthymeilif.Model.Compra;
import com.example.crudthymeilif.Model.Concursant;
import com.example.crudthymeilif.Model.Usuari;
import com.example.crudthymeilif.Service.ConcursantService;
import com.example.crudthymeilif.repository.CompraRepository;
import com.example.crudthymeilif.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Controller
public class HomeController {

    @Autowired
    private UsuariRepository usuariRepository;

    @Autowired
    private ConcursantService concursantService;

    @Autowired
    private CompraRepository compraRepository;

    @Value("${app.upload.dir:data/uploads}")
    private String uploadDir;

    @GetMapping("/")
    public String home() {
        return "redirect:/competiciones";
    }

    @GetMapping("/perfil")
    public String perfil(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String email = userDetails.getUsername();
        usuariRepository.findByCorreu(email).ifPresent(usuari -> {
            model.addAttribute("usuari", usuari);
            concursantService.obtenerConcursantDelUsuari(usuari.getDni()).ifPresent(concursant -> {
                java.util.List<Compra> pendents = compraRepository.findByConcursantAndEstat(concursant, "PENDENT");
                model.addAttribute("inscripcionsPendents", pendents);
            });
        });
        return "perfil";
    }

    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@AuthenticationPrincipal UserDetails userDetails,
                                    @ModelAttribute Usuari usuari) {
        String email = userDetails.getUsername();
        usuariRepository.findByCorreu(email).ifPresent(usuariActual -> {
            usuariActual.setNom(usuari.getNom());
            usuariActual.setTelefon(usuari.getTelefon());
            if (usuari.getNacionalitat() != null && !usuari.getNacionalitat().isEmpty()) {
                usuariActual.setNacionalitat(usuari.getNacionalitat().toUpperCase());
            }
            usuariRepository.save(usuariActual);

            Optional<Concursant> concursantOpt = concursantService.obtenerConcursantDelUsuari(usuariActual.getDni());
            if (concursantOpt.isPresent()) {
                Concursant concursant = concursantOpt.get();
                String[] nombreCompleto = usuari.getNom() != null ? usuari.getNom().split(" ", 2) : new String[]{concursant.getNom(), concursant.getCognom()};
                concursant.setNom(nombreCompleto.length > 0 ? nombreCompleto[0] : concursant.getNom());
                concursant.setCognom(nombreCompleto.length > 1 ? nombreCompleto[1] : concursant.getCognom());
                concursant.setEmail(usuariActual.getCorreu());
                concursant.setTelefon(usuari.getTelefon() != null ? usuari.getTelefon().toString() : concursant.getTelefon());
                concursantService.actualizarConcursant(concursant);
            }
        });
        return "redirect:/perfil";
    }

    @PostMapping("/perfil/foto")
    public String actualitzarFotoPerfil(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestParam("foto") MultipartFile foto) {
        if (foto.isEmpty()) return "redirect:/perfil";

        String email = userDetails.getUsername();
        usuariRepository.findByCorreu(email).ifPresent(usuari -> {
            try {
                Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
                Files.createDirectories(uploadPath);

                // Delete old photo if exists
                if (usuari.getFotoPerfilPath() != null) {
                    Path old = uploadPath.resolve(usuari.getFotoPerfilPath());
                    Files.deleteIfExists(old);
                }

                String ext = "";
                String original = foto.getOriginalFilename();
                if (original != null && original.contains(".")) {
                    ext = original.substring(original.lastIndexOf('.'));
                }
                String filename = "profile_" + UUID.randomUUID() + ext;
                Path dest = uploadPath.resolve(filename);
                Files.copy(foto.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

                usuari.setFotoPerfilPath(filename);
                usuariRepository.save(usuari);
            } catch (IOException e) {
                // Log and continue
                e.printStackTrace();
            }
        });
        return "redirect:/perfil";
    }
}
