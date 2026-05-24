package com.techub.api.service;

import com.techub.api.domain.Bio;
import com.techub.api.repository.BioRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BioService {

    private final BioRepository bioRepository;

    public BioService(BioRepository bioRepository) {
        this.bioRepository = bioRepository;
    }

    public List<Bio> listar(int limit) {
        int pageSize = Math.max(1, limit);
        return bioRepository.findActive(PageRequest.of(0, pageSize, Sort.by(Sort.Direction.ASC, "id"))).getContent();
    }

    public Bio buscarPorDescricao(String description) {
        return bioRepository.findByDescription(description)
                .orElseThrow(() -> new RuntimeException("Bio não encontrada"));
    }
}
