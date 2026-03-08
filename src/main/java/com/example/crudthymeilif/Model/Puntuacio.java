package com.example.crudthymeilif.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "puntuacio", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"wod_id", "concursant_id"})
})
public class Puntuacio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wod_id", nullable = false)
    private Wod wod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concursant_id", nullable = false)
    private Concursant concursant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competicio_id", nullable = false)
    private Competicion competicio;

    // Per WODs de tipus TIME (format mm:ss)
    @Column(name = "temps")
    private String temps;

    // Per WODs de tipus WEIGHT
    @Column(name = "kg")
    private Double kg;

    // Per WODs de tipus REPS
    @Column(name = "reps")
    private Integer reps;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Wod getWod() { return wod; }
    public void setWod(Wod wod) { this.wod = wod; }

    public Concursant getConcursant() { return concursant; }
    public void setConcursant(Concursant concursant) { this.concursant = concursant; }

    public Competicion getCompeticio() { return competicio; }
    public void setCompeticio(Competicion competicio) { this.competicio = competicio; }

    public String getTemps() { return temps; }
    public void setTemps(String temps) { this.temps = temps; }

    public Double getKg() { return kg; }
    public void setKg(Double kg) { this.kg = kg; }

    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Helper: convertir temps string a segons per comparar
    public int getTempsEnSegons() {
        if (temps == null || temps.isEmpty()) return Integer.MAX_VALUE;
        String[] parts = temps.split(":");
        if (parts.length == 2) {
            return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
        }
        return Integer.MAX_VALUE;
    }

    // Helper: obtenir valor visible
    public String getValorVisible() {
        if (temps != null && !temps.isEmpty()) return temps;
        if (kg != null) return kg + " kg";
        if (reps != null) return reps + " reps";
        return "-";
    }
}
