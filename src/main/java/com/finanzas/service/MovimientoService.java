package com.finanzas.service;

import com.finanzas.model.Lote;
import com.finanzas.model.Movimiento;
import com.finanzas.model.TipoMovimiento;
import com.finanzas.repository.MovimientoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimientoService {

    private final MovimientoRepository repository;

    public MovimientoService(MovimientoRepository repository) {
        this.repository = repository;
    }

    public List<Movimiento> listarPorLote(Lote lote) {
        return repository.findByLoteOrderByFechaDescIdDesc(lote);
    }

    public Movimiento guardar(String nombre, TipoMovimiento tipo, Long monto, LocalDateTime fecha, Lote lote) {
        validar(nombre, tipo, monto, fecha);
        return repository.save(new Movimiento(fecha, nombre, tipo, monto, lote));
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    public long totalIngresos(Lote lote) {
        return repository.findByLoteOrderByFechaDescIdDesc(lote).stream()
                .filter(m -> m.getTipo() == TipoMovimiento.INGRESO)
                .mapToLong(Movimiento::getMonto)
                .sum();
    }

    public long totalGastos(Lote lote) {
        return repository.findByLoteOrderByFechaDescIdDesc(lote).stream()
                .filter(m -> m.getTipo() == TipoMovimiento.GASTO)
                .mapToLong(Movimiento::getMonto)
                .sum();
    }

    public long balance(Lote lote) {
        return totalIngresos(lote) - totalGastos(lote);
    }

    private void validar(String nombre, TipoMovimiento tipo, Long monto, LocalDateTime fecha) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo es obligatorio.");
        }
        if (monto == null || monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que 0.");
        }
        if (fecha == null || fecha.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha no puede ser futura.");
        }
    }
}