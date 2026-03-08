package com.example.crudthymeilif.repository;

import com.example.crudthymeilif.Model.Competicion;
import com.example.crudthymeilif.Model.Concursant;
import com.example.crudthymeilif.Model.Puntuacio;
import com.example.crudthymeilif.Model.Wod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PuntuacioRepository extends JpaRepository<Puntuacio, Long> {
    List<Puntuacio> findByCompeticio(Competicion competicio);
    List<Puntuacio> findByWod(Wod wod);
    List<Puntuacio> findByWodOrderByTempsAsc(Wod wod);
    List<Puntuacio> findByWodOrderByKgDesc(Wod wod);
    List<Puntuacio> findByWodOrderByRepsDesc(Wod wod);
    Optional<Puntuacio> findByWodAndConcursant(Wod wod, Concursant concursant);
    boolean existsByWodAndConcursant(Wod wod, Concursant concursant);
    List<Puntuacio> findByConcursant(Concursant concursant);
    void deleteByWodAndConcursant(Wod wod, Concursant concursant);
}
