package com.example.crudthymeilif.repository;

import com.example.crudthymeilif.Model.Concursant;
import com.example.crudthymeilif.Model.Wod;
import com.example.crudthymeilif.Model.WodComplet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WodCompletRepository extends JpaRepository<WodComplet, Long> {
    List<WodComplet> findByWod(Wod wod);
    List<WodComplet> findByConcursant(Concursant concursant);
    Optional<WodComplet> findByWodAndConcursant(Wod wod, Concursant concursant);
    boolean existsByWodAndConcursant(Wod wod, Concursant concursant);
    List<WodComplet> findByWodCompeticion(com.example.crudthymeilif.Model.Competicion competicion);
    void deleteByWodAndConcursant(Wod wod, Concursant concursant);
}
