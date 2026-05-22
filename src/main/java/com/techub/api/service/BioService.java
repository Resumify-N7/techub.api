package com.techub.api.service;

import com.techub.api.domain.Bio;
import com.techub.api.repository.BioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BioService {

    private final BioRepository bioRepository;

    public BioService(BioRepository bioRepository) {
        this.bioRepository = bioRepository;
    }

    public List<Bio> listar() {
        return bioRepository.findAllByOrderByIdAsc();
    }

    public Bio buscarPorDescricao(String description) {
        return bioRepository.findByDescription(description)
                .orElseThrow(() -> new RuntimeException("Bio não encontrada"));
    }
}
