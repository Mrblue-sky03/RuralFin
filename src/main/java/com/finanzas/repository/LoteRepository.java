package com.finanzas.repository;

import com.finanzas.model.Lote;
import com.finanzas.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {
    List<Lote> findByCategoriaOrderByFechaDescIdDesc(Categoria categoria);
}