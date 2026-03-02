package com.example.crudthymeilif.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "exercici")
public class Exercici {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nom", nullable = false)
    @NotBlank(message = "El nom de l'exercici és obligatori.")
    private String nom;

    @Column(name = "repeticions")
    private Integer repeticions;

    @Column(name = "ordre")
    private Integer ordre;

    @Column(name = "notes", length = 300)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dificultat_wod_id", nullable = false)
    private DificultatWod dificultatWod;

    // Constructors
    public Exercici() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Integer getRepeticions() { return repeticions; }
    public void setRepeticions(Integer repeticions) { this.repeticions = repeticions; }

    public Integer getOrdre() { return ordre; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public DificultatWod getDificultatWod() { return dificultatWod; }
    public void setDificultatWod(DificultatWod dificultatWod) { this.dificultatWod = dificultatWod; }
}
