package com.example.appcrossfit.repository;

import com.example.appcrossfit.model.Competicion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompeticionRepository extends JpaRepository<Competicion, Long> {
}

