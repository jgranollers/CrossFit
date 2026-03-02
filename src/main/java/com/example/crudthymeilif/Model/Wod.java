package com.example.crudthymeilif.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wod")
public class Wod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nom", nullable = false)
    @NotBlank(message = "El nom del WOD és obligatori.")
    private String nom;

    // INDIVIDUAL o GRUP
    @Column(name = "modalitat", nullable = false)
    private String modalitat;

    // Null si INDIVIDUAL; HH, DD, HD si GRUP
    @Column(name = "subtipus_grup")
    private String subtipusGrup;

    @Column(name = "ordre")
    private Integer ordre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competicion_id", nullable = false)
    private Competicion competicion;

    @OneToMany(mappedBy = "wod", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("dificultat ASC")
    private List<DificultatWod> dificultats = new ArrayList<>();

    // Constructors
    public Wod() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getModalitat() { return modalitat; }
    public void setModalitat(String modalitat) { this.modalitat = modalitat; }

    public String getSubtipusGrup() { return subtipusGrup; }
    public void setSubtipusGrup(String subtipusGrup) { this.subtipusGrup = subtipusGrup; }

    public Integer getOrdre() { return ordre; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }

    public Competicion getCompeticion() { return competicion; }
    public void setCompeticion(Competicion competicion) { this.competicion = competicion; }

    public List<DificultatWod> getDificultats() { return dificultats; }
    public void setDificultats(List<DificultatWod> dificultats) { this.dificultats = dificultats; }

    // Helper: etiqueta de modalitat llegible
    public String getModalitaNom() {
        if ("GRUP".equals(modalitat) && subtipusGrup != null) {
            return switch (subtipusGrup) {
                case "HH" -> "Grup Home-Home";
                case "DD" -> "Grup Dona-Dona";
                case "HD" -> "Grup Home-Dona";
                default -> "Grup";
            };
        }
        return "Individual";
    }
}
