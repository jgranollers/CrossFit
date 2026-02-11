package com.example.crudthymeilif.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "llibre")
public class Llibre {
    @Id
    @Column(name = "Isbn", nullable = false)
    private String isbn;

    @Column(name = "titol")
    private String titol;

    @Column(name = "categoria")
    private String categoria;

    @Column(name = "preu")
    private Double preu;

    @Column(name = "editorial")
    private String editorial;

    @Column(name = "autor")
    private String autor;

    @OneToMany(mappedBy = "isbn")
    @JsonIgnore
    private List<Prestec> prestecs;

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitol() {
        return titol;
    }

    public void setTitol(String titol) {
        this.titol = titol;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Double getPreu() {
        return preu;
    }

    public void setPreu(Double preu) {
        this.preu = preu;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public List<Prestec> getPrestecs() {
        return prestecs;
    }

    public void setPrestecs(List<Prestec> prestecs) {
        this.prestecs = prestecs;
    }

}