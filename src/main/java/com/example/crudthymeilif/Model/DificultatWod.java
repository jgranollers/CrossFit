package com.example.crudthymeilif.Model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dificultat_wod")
public class DificultatWod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // DIFICIL, INTERMIG, FACIL
    @Column(name = "dificultat", nullable = false)
    private String dificultat;

    // FOR_TIME, AMRAP, FOR_REPS
    @Column(name = "tipus_puntuacio")
    private String tipusPuntuacio;

    @Column(name = "temps_limit")
    private Integer tempsLimit; // minuts

    @Column(name = "rondes")
    private Integer rondes;

    @Column(name = "descripcio", length = 500)
    private String descripcio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wod_id", nullable = false)
    private Wod wod;

    @OneToMany(mappedBy = "dificultatWod", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    private List<Exercici> exercicis = new ArrayList<>();

    // Constructors
    public DificultatWod() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDificultat() { return dificultat; }
    public void setDificultat(String dificultat) { this.dificultat = dificultat; }

    public String getTipusPuntuacio() { return tipusPuntuacio; }
    public void setTipusPuntuacio(String tipusPuntuacio) { this.tipusPuntuacio = tipusPuntuacio; }

    public Integer getTempsLimit() { return tempsLimit; }
    public void setTempsLimit(Integer tempsLimit) { this.tempsLimit = tempsLimit; }

    public Integer getRondes() { return rondes; }
    public void setRondes(Integer rondes) { this.rondes = rondes; }

    public String getDescripcio() { return descripcio; }
    public void setDescripcio(String descripcio) { this.descripcio = descripcio; }

    public Wod getWod() { return wod; }
    public void setWod(Wod wod) { this.wod = wod; }

    public List<Exercici> getExercicis() { return exercicis; }
    public void setExercicis(List<Exercici> exercicis) { this.exercicis = exercicis; }

    // Helper: color Bootstrap per dificultat
    public String getColorBadge() {
        return switch (dificultat != null ? dificultat : "") {
            case "DIFICIL" -> "danger";
            case "INTERMIG" -> "warning";
            case "FACIL" -> "success";
            default -> "secondary";
        };
    }

    public String getDificultatNom() {
        return switch (dificultat != null ? dificultat : "") {
            case "DIFICIL" -> "Difícil";
            case "INTERMIG" -> "Intermig";
            case "FACIL" -> "Fàcil";
            default -> dificultat;
        };
    }
}
