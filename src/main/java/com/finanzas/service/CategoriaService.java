package com.finanzas.service;

import com.finanzas.model.Categoria;
import com.finanzas.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    public List<Categoria> listarTodas() {
        return repository.findAll();
    }

    public Categoria guardar(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        return repository.save(new Categoria(nombre.trim()));
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}