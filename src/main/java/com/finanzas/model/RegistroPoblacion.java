package com.finanzas.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "registros_poblacion")
public class RegistroPoblacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long enfermos = 0L;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long heridos = 0L;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long muertos = 0L;

    @ManyToOne
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    public RegistroPoblacion() {}

    public RegistroPoblacion(LocalDate fecha, Long enfermos, Long heridos, Long muertos, Lote lote) {
        this.fecha = fecha;
        this.enfermos = enfermos;
        this.heridos = heridos;
        this.muertos = muertos;
        this.lote = lote;
    }

    public Long getId() { return id; }
    public LocalDate getFecha() { return fecha; }
    public Long getEnfermos() { return enfermos; }
    public Long getHeridos() { return heridos; }
    public Long getMuertos() { return muertos; }
    public Lote getLote() { return lote; }

    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setEnfermos(Long enfermos) { this.enfermos = enfermos; }
    public void setHeridos(Long heridos) { this.heridos = heridos; }
    public void setMuertos(Long muertos) { this.muertos = muertos; }
    public void setLote(Lote lote) { this.lote = lote; }
}