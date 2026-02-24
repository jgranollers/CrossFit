package com.example.crudthymeilif.repository;

import com.example.crudthymeilif.Model.Usuari;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuariRepository extends JpaRepository<Usuari, String> {
    Optional<Usuari> findByCorreu(String correu);
}
