package com.example.crudthymeilif.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "competicion")
public class Competicion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "tipus_competicio")
    @NotBlank(message = "El tipus de competicio es obligatori.")
    private String tipusCompeticio;

    @Column(name = "data_competicio")
    @NotNull(message = "La data de la competicio es obligatoria.")
    @FutureOrPresent(message = "La data no pot ser anterior a avui.")
    private LocalDate dataCompeticio;

    @Column(name = "localitat")
    private String localitat;

    @Column(name = "descripcio", length = 1000)
    private String descripcio;

    @Column(name = "preu_inscripcio")
    private Double preuInscripcio;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "estat")
    private String estat; // OBERTA, TANCADA, FINALITZADA, CANCEL·LADA

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (estat == null) {
            estat = "OBERTA";
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTipusCompeticio() {
        return tipusCompeticio;
    }

    public void setTipusCompeticio(String tipusCompeticio) {
        this.tipusCompeticio = tipusCompeticio;
    }

    public LocalDate getDataCompeticio() {
        return dataCompeticio;
    }

    public void setDataCompeticio(LocalDate dataCompeticio) {
        this.dataCompeticio = dataCompeticio;
    }

    public String getLocalitat() {
        return localitat;
    }

    public void setLocalitat(String localitat) {
        this.localitat = localitat;
    }

    public String getDescripcio() {
        return descripcio;
    }

    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }

    public Double getPreuInscripcio() {
        return preuInscripcio;
    }

    public void setPreuInscripcio(Double preuInscripcio) {
        this.preuInscripcio = preuInscripcio;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public String getEstat() {
        return estat;
    }

    public void setEstat(String estat) {
        this.estat = estat;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
