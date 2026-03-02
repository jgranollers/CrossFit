package com.example.crudthymeilif.repository;

import com.example.crudthymeilif.Model.DificultatWod;
import com.example.crudthymeilif.Model.Wod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DificultatWodRepository extends JpaRepository<DificultatWod, Long> {
    List<DificultatWod> findByWodOrderByDificultatAsc(Wod wod);
}
