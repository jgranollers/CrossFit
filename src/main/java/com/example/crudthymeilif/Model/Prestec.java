package com.example.crudthymeilif.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "prestec")
public class Prestec {
    @Id
    @Column(name = "codi", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ISBN")
    @JsonIgnoreProperties({"prestecs", "hibernateLazyInitializer", "handler"})
    private Llibre isbn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DNI")
    @JsonIgnoreProperties({"prestecs", "hibernateLazyInitializer", "handler"})
    private Usuari dni;

    @Column(name = "data_prestec")
    private LocalDate dataPrestec;

    @Column(name = "data_retorn")
    private LocalDate dataRetorn;

    @Column(name = "retornat")
    private Boolean retornat;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Llibre getIsbn() {
        return isbn;
    }

    public void setIsbn(Llibre isbn) {
        this.isbn = isbn;
    }

    public Usuari getDni() {
        return dni;
    }

    public void setDni(Usuari dni) {
        this.dni = dni;
    }

    public LocalDate getDataPrestec() {
        return dataPrestec;
    }

    public void setDataPrestec(LocalDate dataPrestec) {
        this.dataPrestec = dataPrestec;
    }

    public LocalDate getDataRetorn() {
        return dataRetorn;
    }

    public void setDataRetorn(LocalDate dataRetorn) {
        this.dataRetorn = dataRetorn;
    }

    public Boolean getRetornat() {
        return retornat;
    }

    public void setRetornat(Boolean retornat) {
        this.retornat = retornat;
    }

}