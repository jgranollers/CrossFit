package com.example.crudthymeilif.Service;

import com.example.crudthymeilif.Model.Usuari;
import com.example.crudthymeilif.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UsuariRepository usuariRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuari registerUser(String dni, String nom, String correu, String password, Integer telefon) {
        // Verificar si el usuario ya existe
        if (usuariRepository.findById(dni).isPresent()) {
            throw new RuntimeException("El DNI ja està registrat");
        }

        if (usuariRepository.findByCorreu(correu).isPresent()) {
            throw new RuntimeException("El correu ja està registrat");
        }

        // Crear nuevo usuario - registro directo sin verificación
        Usuari usuari = new Usuari();
        usuari.setDni(dni);
        usuari.setNom(nom);
        usuari.setCorreu(correu);
        usuari.setPassword(passwordEncoder.encode(password));
        usuari.setTelefon(telefon);
        usuari.setToken(UUID.randomUUID().toString());
        usuari.setVerified(true);  // Verificado automáticamente
        usuari.setEnabled(true);

        // Guardar usuario
        usuari = usuariRepository.save(usuari);

        return usuari;
    }

    public Optional<Usuari> findByCorreu(String correu) {
        return usuariRepository.findByCorreu(correu);
    }
}
