package com.example.appcrossfit.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "competicions")
public class Competicion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String tipus; // Online, Presencial, Híbrid

    @Column(name = "data_inici_inscripcio")
    private LocalDate dataIniciInscripcio;

    @Column(name = "data_fi_inscripcio")
    private LocalDate dataFiInscripcio;

    @Column(nullable = false)
    private String ubicacio;

    @Column(precision = 10, scale = 2)
    private BigDecimal preu;

    private Boolean activa = true;

    // Relación con Equipos: Una competición tiene muchos equipos
    @OneToMany(mappedBy = "competicion", cascade = CascadeType.ALL)
    private List<Equip> equips;
}