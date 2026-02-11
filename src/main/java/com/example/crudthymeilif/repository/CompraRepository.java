package com.example.crudthymeilif.repository;

import com.example.crudthymeilif.Model.Compra;
import com.example.crudthymeilif.Model.Usuari;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
    List<Compra> findByUsuari(Usuari usuari);
    List<Compra> findByUsuariDni(String dni);
    List<Compra> findByEstat(String estat);
}
