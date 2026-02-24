package com.example.crudthymeilif.Service;

import com.example.crudthymeilif.Model.Usuari;
import com.example.crudthymeilif.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuariRepository usuariRepository;

    @Override
    public UserDetails loadUserByUsername(String correu) throws UsernameNotFoundException {
        Usuari usuari = usuariRepository.findByCorreu(correu)
                .orElseThrow(() -> new UsernameNotFoundException("Usuari no trobat amb correu: " + correu));

        if (!usuari.getVerified()) {
            throw new UsernameNotFoundException("Usuari no verificat. Comprova el teu correu.");
        }

        if (!usuari.getEnabled()) {
            throw new UsernameNotFoundException("Compte desactivat.");
        }

        String rol = usuari.getRol() != null ? usuari.getRol() : "USUARI";
        return User.builder()
                .username(usuari.getCorreu())
                .password(usuari.getPassword())
                .roles(rol)   // Spring prefixes with ROLE_ automatically
                .build();
    }
}
