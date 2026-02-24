package com.example.crudthymeilif.repository;

import com.example.crudthymeilif.Model.Competicion;
import com.example.crudthymeilif.Model.Compra;
import com.example.crudthymeilif.Model.Concursant;
import com.example.crudthymeilif.Model.Usuari;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
    List<Compra> findByUsuari(Usuari usuari);
    List<Compra> findByUsuariDni(String dni);
    List<Compra> findByEstat(String estat);
    List<Compra> findByCompeticio(Competicion competicio);
    long countByCompeticio(Competicion competicio);
    long countByCompeticioAndEstat(Competicion competicio, String estat);
    Optional<Compra> findByCompeticioAndConcursant(Competicion competicio, Concursant concursant);
    List<Compra> findByConcursantAndEstat(Concursant concursant, String estat);
    Optional<Compra> findByStripePaymentId(String stripePaymentId);
}
