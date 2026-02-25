package com.finanzas.repository;

import com.finanzas.model.Lote;
import com.finanzas.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    List<Movimiento> findByLoteOrderByFechaDescIdDesc(Lote lote);
}