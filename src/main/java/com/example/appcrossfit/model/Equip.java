package com.example.appcrossfit.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "equips")
public class Equip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_equip", nullable = false)
    private String nomEquip;

    @Column(name = "box_entrenament")
    private String boxEntrenament;

    // Conexión con la competición
    @ManyToOne
    @JoinColumn(name = "competicion_id")
    private Competicion competicion;

    // Relación con Atletas
    @OneToMany(mappedBy = "equip", cascade = CascadeType.ALL)
    private List<Atlete> atletes;
}