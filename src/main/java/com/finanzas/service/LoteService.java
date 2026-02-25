package com.finanzas.service;

import com.finanzas.model.Categoria;
import com.finanzas.model.Lote;
import com.finanzas.repository.LoteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoteService {

    private final LoteRepository repository;

    public LoteService(LoteRepository repository) {
        this.repository = repository;
    }

    public List<Lote> listarPorCategoria(Categoria categoria) {
        return repository.findByCategoriaOrderByFechaDescIdDesc(categoria);
    }

    public Lote guardar(String nombre, LocalDate fecha, Categoria categoria) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (fecha == null || fecha.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha no puede ser futura.");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría es obligatoria.");
        }
        return repository.save(new Lote(nombre.trim(), fecha, categoria));
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}