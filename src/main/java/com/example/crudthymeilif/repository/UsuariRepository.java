package com.example.crudthymeilif.repository;

import com.example.crudthymeilif.Model.Usuari;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuariRepository extends JpaRepository<Usuari, String> {
}
