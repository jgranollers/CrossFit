package com.example.crudthymeilif.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "compra")
public class Compra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dni_usuari", referencedColumnName = "DNI")
    private Usuari usuari;

    @ManyToOne
    @JoinColumn(name = "isbn_llibre", referencedColumnName = "Isbn")
    private Llibre llibre;

    @Column(name = "data_compra")
    private LocalDateTime dataCompra;

    @Column(name = "preu_pagat")
    private Double preuPagat;

    @Column(name = "stripe_payment_id")
    private String stripePaymentId;

    @Column(name = "estat")
    private String estat; // PENDENT, COMPLETAT, CANCEL·LAT

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuari getUsuari() {
        return usuari;
    }

    public void setUsuari(Usuari usuari) {
        this.usuari = usuari;
    }

    public Llibre getLlibre() {
        return llibre;
    }

    public void setLlibre(Llibre llibre) {
        this.llibre = llibre;
    }

    public LocalDateTime getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(LocalDateTime dataCompra) {
        this.dataCompra = dataCompra;
    }

    public Double getPreuPagat() {
        return preuPagat;
    }

    public void setPreuPagat(Double preuPagat) {
        this.preuPagat = preuPagat;
    }

    public String getStripePaymentId() {
        return stripePaymentId;
    }

    public void setStripePaymentId(String stripePaymentId) {
        this.stripePaymentId = stripePaymentId;
    }

    public String getEstat() {
        return estat;
    }

    public void setEstat(String estat) {
        this.estat = estat;
    }
}
