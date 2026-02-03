package com.example.appcrossfit.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "atletes")
public class Atlete {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nom_complet", nullable = false)
    private String nomComplet;

    @Column(name = "pais", length = 100)
    private String pais;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @ColumnDefault("0")
    @Column(name = "es_capita")
    private Boolean esCapita;

    @Lob
    @Column(name = "talla_samarreta")
    private String tallaSamarreta;

    @Column(name = "accepta_politica_privacitat", nullable = false)
    private Boolean acceptaPoliticaPrivacitat = false;

    @Column(name = "equip_id")
    private Long equipId;

}