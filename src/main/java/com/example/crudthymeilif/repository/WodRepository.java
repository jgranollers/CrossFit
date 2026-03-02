package com.example.crudthymeilif.repository;

import com.example.crudthymeilif.Model.Competicion;
import com.example.crudthymeilif.Model.Wod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WodRepository extends JpaRepository<Wod, Long> {
    List<Wod> findByCompeticionOrderByOrdreAsc(Competicion competicion);
    long countByCompeticion(Competicion competicion);
}
