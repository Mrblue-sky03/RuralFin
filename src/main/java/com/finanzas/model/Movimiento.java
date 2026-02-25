package com.finanzas.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos")
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimiento tipo;

    @Column(nullable = false)
    private Long monto;

    @ManyToOne
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    public Movimiento() {}

    public Movimiento(LocalDateTime fecha, String nombre, TipoMovimiento tipo, Long monto, Lote lote) {
        this.fecha = fecha;
        this.nombre = nombre;
        this.tipo = tipo;
        this.monto = monto;
        this.lote = lote;
    }

    public Long getId() { return id; }
    public LocalDateTime getFecha() { return fecha; }
    public String getNombre() { return nombre; }
    public TipoMovimiento getTipo() { return tipo; }
    public Long getMonto() { return monto; }
    public Lote getLote() { return lote; }

    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setTipo(TipoMovimiento tipo) { this.tipo = tipo; }
    public void setMonto(Long monto) { this.monto = monto; }
    public void setLote(Lote lote) { this.lote = lote; }
}