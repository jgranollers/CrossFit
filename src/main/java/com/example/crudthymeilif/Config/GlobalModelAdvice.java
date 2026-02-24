package com.example.crudthymeilif.Config;

import com.example.crudthymeilif.Model.Usuari;
import com.example.crudthymeilif.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    @Autowired
    private UsuariRepository usuariRepository;

    @Autowired
    private FlagHelper flagHelper;

    private Usuari getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return usuariRepository.findByCorreu(authentication.getName()).orElse(null);
        }
        return null;
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @ModelAttribute("currentUserNacionalitat")
    public String currentUserNacionalitat(Authentication authentication) {
        Usuari u = getCurrentUser(authentication);
        return u != null ? u.getNacionalitat() : null;
    }

    @ModelAttribute("currentUserFlag")
    public String currentUserFlag(Authentication authentication) {
        Usuari u = getCurrentUser(authentication);
        return u != null ? flagHelper.getFlag(u.getNacionalitat()) : "";
    }

    @ModelAttribute("currentUserFoto")
    public String currentUserFoto(Authentication authentication) {
        Usuari u = getCurrentUser(authentication);
        return (u != null && u.getFotoPerfilPath() != null) ? "/uploads/" + u.getFotoPerfilPath() : null;
    }
}
