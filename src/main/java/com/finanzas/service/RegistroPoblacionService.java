package com.finanzas.service;

import com.finanzas.model.Lote;
import com.finanzas.model.RegistroPoblacion;
import com.finanzas.repository.LoteRepository;
import com.finanzas.repository.RegistroPoblacionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RegistroPoblacionService {

    private final RegistroPoblacionRepository repository;
    private final LoteRepository loteRepository;

    public RegistroPoblacionService(RegistroPoblacionRepository repository, LoteRepository loteRepository) {
        this.repository = repository;
        this.loteRepository = loteRepository;
    }

    public List<RegistroPoblacion> listarPorLote(Lote lote) {
        return repository.findByLoteOrderByFechaDescIdDesc(lote);
    }

    public RegistroPoblacion guardar(LocalDate fecha, Long enfermos, Long heridos, Long muertos, Lote lote) {
        if (fecha == null || fecha.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha no puede ser futura.");
        }
        enfermos = enfermos == null ? 0L : enfermos;
        heridos = heridos == null ? 0L : heridos;
        muertos = muertos == null ? 0L : muertos;
        if (enfermos < 0 || heridos < 0 || muertos < 0) {
            throw new IllegalArgumentException("Los valores no pueden ser negativos.");
        }
        return repository.save(new RegistroPoblacion(fecha, enfermos, heridos, muertos, lote));
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    public long totalMuertos(Lote lote) {
        return repository.findByLoteOrderByFechaDescIdDesc(lote).stream()
                .mapToLong(RegistroPoblacion::getMuertos)
                .sum();
    }

    public long poblacionFinal(Lote lote) {
        if (lote.getPoblacionInicial() == null) return 0L;
        return lote.getPoblacionInicial() - totalMuertos(lote);
    }

    public void guardarPoblacionInicial(Lote lote, Long poblacionInicial) {
        if (poblacionInicial == null || poblacionInicial <= 0) {
            throw new IllegalArgumentException("La población inicial debe ser mayor que 0.");
        }
        lote.setPoblacionInicial(poblacionInicial);
        loteRepository.save(lote);
    }
}