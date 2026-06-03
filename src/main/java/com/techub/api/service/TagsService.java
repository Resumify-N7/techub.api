package com.techub.api.service;

import com.techub.api.domain.Tags;
import com.techub.api.repository.TagsRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagsService {

    private final TagsRepository tagsRepository;

    public TagsService(TagsRepository tagsRepository) {
        this.tagsRepository = tagsRepository;
    }

    public Tags criar(Tags tag) {
        validarTag(tag, null);

        tag.setAtivo(true);
        return tagsRepository.save(tag);
    }

    public List<Tags> listar(int limit) {
        int pageSize = Math.max(1, limit);
        return tagsRepository.findActive(PageRequest.of(0, pageSize, Sort.by(Sort.Direction.ASC, "id")))
                .getContent();
    }

    public List<Tags> listarAtivos(int limit) {
        int pageSize = Math.max(1, limit);
        return tagsRepository.findByAtivoTrue(PageRequest.of(0, pageSize, Sort.by(Sort.Direction.ASC, "id")))
                .getContent();
    }

    public List<Tags> listarDesativados(int limit) {
        int pageSize = Math.max(1, limit);
        return tagsRepository.findByAtivoFalse(PageRequest.of(0, pageSize, Sort.by(Sort.Direction.ASC, "id")))
                .getContent();
    }

    public Tags buscarPorId(Long id) {
        return tagsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag não encontrada"));
    }

    public Tags atualizar(Long id, Tags payload) {
        Tags existing = buscarPorId(id);

        validarTag(payload, id);

        if (payload.getName() != null && !payload.getName().isBlank()) {
            existing.setName(payload.getName());
        }

        return tagsRepository.save(existing);
    }

    public void atualizar_status(Long id) {
        Tags tag = buscarPorId(id);
        tag.setAtivo(!Boolean.TRUE.equals(tag.getAtivo()));
        tagsRepository.save(tag);
    }

    public void deletar(Long id) {
        Tags tag = buscarPorId(id);
        tag.setAtivo(false);
        tagsRepository.save(tag);
    }

    private void validarTag(Tags tag, Long id) {
        if (tag == null) {
            throw new RuntimeException("Tag é obrigatória");
        }

        if (tag.getName() == null || tag.getName().isBlank()) {
            throw new RuntimeException("Nome da tag é obrigatório");
        }

        if (id == null) {
            if (tagsRepository.existsByNameIgnoreCase(tag.getName())) {
                throw new RuntimeException("Nome de tag já usado");
            }
        } else if (tagsRepository.existsByNameIgnoreCaseAndIdNot(tag.getName(), id)) {
            throw new RuntimeException("Nome de tag já usado");
        }
    }
}