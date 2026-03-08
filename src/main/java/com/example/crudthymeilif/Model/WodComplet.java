package com.example.crudthymeilif.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wod_complet", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"wod_id", "concursant_id"})
})
public class WodComplet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wod_id", nullable = false)
    private Wod wod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concursant_id", nullable = false)
    private Concursant concursant;

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
