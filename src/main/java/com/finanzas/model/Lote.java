package com.finanzas.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "lotes")
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column
    private Long poblacionInicial;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL)
    private List<Movimiento> movimientos;

    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL)
    private List<RegistroPoblacion> registrosPoblacion;

    public Lote() {}

    public Lote(String nombre, LocalDate fecha, Categoria categoria) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.categoria = categoria;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public LocalDate getFecha() { return fecha; }
    public Categoria getCategoria() { return categoria; }
    public Long getPoblacionInicial() { return poblacionInicial; }
    public List<Movimiento> getMovimientos() { return movimientos; }
    public List<RegistroPoblacion> getRegistrosPoblacion() { return registrosPoblacion; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public void setPoblacionInicial(Long poblacionInicial) { this.poblacionInicial = poblacionInicial; }
}