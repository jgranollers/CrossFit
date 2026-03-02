package com.example.crudthymeilif.repository;

import com.example.crudthymeilif.Model.DificultatWod;
import com.example.crudthymeilif.Model.Exercici;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciciRepository extends JpaRepository<Exercici, Long> {
    List<Exercici> findByDificultatWodOrderByOrdreAsc(DificultatWod dificultatWod);
}
