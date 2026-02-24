package com.example.crudthymeilif.repository;

import com.example.crudthymeilif.Model.Competicion;
import com.example.crudthymeilif.Model.Concursant;
import com.example.crudthymeilif.Model.Resultat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResultatRepository extends JpaRepository<Resultat, Long> {
    List<Resultat> findByCompeticio(Competicion competicio);
    List<Resultat> findByConcursant(Concursant concursant);
    List<Resultat> findByCompeticioOrderByPosicioAsc(Competicion competicio);
    Optional<Resultat> findByCompeticioAndConcursant(Competicion competicio, Concursant concursant);
}
