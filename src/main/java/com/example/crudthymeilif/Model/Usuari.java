package com.example.crudthymeilif.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "usuari")
public class Usuari {
    @Id
    @Column(name = "DNI", nullable = false, length = 9)
    private String dni;

    @Column(name = "nom")
    private String nom;

    @Column(name = "telefon")
    private Integer telefon;

    @Column(name = "correu")
    private String correu;

    @Column(name = "token", nullable = false)
    private String token;

    @OneToMany(mappedBy = "dni")
    @JsonIgnore
    private List<Prestec> prestecs;

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getTelefon() {
        return telefon;
    }

    public void setTelefon(Integer telefon) {
        this.telefon = telefon;
    }

    public String getCorreu() {
        return correu;
    }

    public void setCorreu(String correu) {
        this.correu = correu;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Prestec> getPrestecs() {
        return prestecs;
    }

    public void setPrestecs(List<Prestec> prestecs) {
        this.prestecs = prestecs;
    }

}