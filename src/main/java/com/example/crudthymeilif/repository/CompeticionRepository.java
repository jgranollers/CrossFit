package com.example.crudthymeilif.repository;

import com.example.crudthymeilif.Model.Competicion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CompeticionRepository extends JpaRepository<Competicion, Long> {
    List<Competicion> findByEstat(String estat);
    List<Competicion> findByDataCompeticioBetween(LocalDate dataInici, LocalDate dataFi);
    List<Competicion> findByLocalitat(String localitat);
}
