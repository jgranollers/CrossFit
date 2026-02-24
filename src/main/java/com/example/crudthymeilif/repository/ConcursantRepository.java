package com.example.crudthymeilif.repository;

import com.example.crudthymeilif.Model.Concursant;
import com.example.crudthymeilif.Model.Usuari;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConcursantRepository extends JpaRepository<Concursant, Long> {
    Optional<Concursant> findByUsuariDni(String dni);
    List<Concursant> findByUsuari(Usuari usuari);
    List<Concursant> findByCategoria(String categoria);
    List<Concursant> findBySexe(String sexe);
}
