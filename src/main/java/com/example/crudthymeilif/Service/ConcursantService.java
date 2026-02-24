package com.example.crudthymeilif.Service;

import com.example.crudthymeilif.Model.Concursant;
import com.example.crudthymeilif.Model.Usuari;
import com.example.crudthymeilif.repository.ConcursantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConcursantService {

    @Autowired
    private ConcursantRepository concursantRepository;

    /**
     * Crear o obtener el concursante del usuario actual
     */
    public Concursant crearConcursantDelUsuari(Usuari usuari, String categoria, String sexe, Integer edat) {
        // Verificar si el usuario ya tiene un concursante
        Optional<Concursant> existente = concursantRepository.findByUsuariDni(usuari.getDni());
        if (existente.isPresent()) {
            return null; // Usuario ya tiene concursante
        }

        // Crear nuevo concursante con datos del usuario
        Concursant concursant = new Concursant();
        concursant.setNom(usuari.getNom());
        concursant.setCognom("-"); // Sin apellido en usuari, se deja "-"
        concursant.setEmail(usuari.getCorreu());
        concursant.setTelefon(usuari.getTelefon() != null ? usuari.getTelefon().toString() : null);
        concursant.setUsuari(usuari);
        concursant.setCategoria(categoria != null && !categoria.isEmpty() ? categoria : "RX");
        concursant.setSexe(sexe != null && !sexe.isEmpty() ? sexe : null);
        concursant.setEdat(edat);

        return concursantRepository.save(concursant);
    }

    /**
     * Verificar si el usuario ya tiene un concursante
     */
    public boolean usuariTieneConcursant(String dni) {
        return concursantRepository.findByUsuariDni(dni).isPresent();
    }

    /**
     * Obtener el concursante del usuario
     */
    public Optional<Concursant> obtenerConcursantDelUsuari(String dni) {
        return concursantRepository.findByUsuariDni(dni);
    }

    /**
     * Actualizar concursante
     */
    public Concursant actualizarConcursant(Concursant concursant) {
        return concursantRepository.save(concursant);
    }
}
