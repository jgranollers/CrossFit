package com.example.crudthymeilif.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resultat")
public class Resultat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "competicio_id", nullable = false)
    private Competicion competicio;

    @ManyToOne
    @JoinColumn(name = "concursant_id", nullable = false)
    private Concursant concursant;

    @Column(name = "posicio")
    private Integer posicio;

    @Column(name = "temps")
    private String temps; // Format: HH:MM:SS o MM:SS

    @Column(name = "puntuacio")
    private Integer puntuacio;

    @Column(name = "reps_completades")
    private Integer repsCompletades;

    @Column(name = "comentaris", length = 500)
    private String comentaris;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Competicion getCompeticio() {
        return competicio;
    }

    public void setCompeticio(Competicion competicio) {
        this.competicio = competicio;
    }

    public Concursant getConcursant() {
        return concursant;
    }

    public void setConcursant(Concursant concursant) {
        this.concursant = concursant;
    }

    public Integer getPosicio() {
        return posicio;
    }

    public void setPosicio(Integer posicio) {
        this.posicio = posicio;
    }

    public String getTemps() {
        return temps;
    }

    public void setTemps(String temps) {
        this.temps = temps;
    }

    public Integer getPuntuacio() {
        return puntuacio;
    }

    public void setPuntuacio(Integer puntuacio) {
        this.puntuacio = puntuacio;
    }

    public Integer getRepsCompletades() {
        return repsCompletades;
    }

    public void setRepsCompletades(Integer repsCompletades) {
        this.repsCompletades = repsCompletades;
    }

    public String getComentaris() {
        return comentaris;
    }

    public void setComentaris(String comentaris) {
        this.comentaris = comentaris;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
