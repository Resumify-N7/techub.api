package com.techub.api.service;

import com.techub.api.domain.Badge;
import com.techub.api.repository.BadgeRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BadgeService {

    private final BadgeRepository badgeRepository;

    public BadgeService(BadgeRepository badgeRepository) {
        this.badgeRepository = badgeRepository;
    }

    public Badge criar(Badge badge) {
        validarBadge(badge, null);

        badge.setAtivo(true);
        return badgeRepository.save(badge);
    }

    public List<Badge> listar(int limit) {
        int pageSize = Math.max(1, limit);
        return badgeRepository.findActive(PageRequest.of(0, pageSize, Sort.by(Sort.Direction.ASC, "id")))
                .getContent();
    }

    public List<Badge> listarAtivos(int limit) {
        int pageSize = Math.max(1, limit);
        return badgeRepository.findByAtivoTrue(PageRequest.of(0, pageSize, Sort.by(Sort.Direction.ASC, "id")))
                .getContent();
    }

    public List<Badge> listarDesativados(int limit) {
        int pageSize = Math.max(1, limit);
        return badgeRepository.findByAtivoFalse(PageRequest.of(0, pageSize, Sort.by(Sort.Direction.ASC, "id")))
                .getContent();
    }

    public Badge buscarPorId(Long id) {
        return badgeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Badge não encontrado"));
    }

    public Badge buscarBadgeDeCurtidas() {
        return badgeRepository.findByNameIgnoreCaseAndAtivoTrue("Resumo Popular")
                .orElseThrow(() -> new RuntimeException("Badge de curtidas não configurado"));
    }

    public Badge atualizar(Long id, Badge payload) {
        Badge existing = buscarPorId(id);

        validarBadge(payload, id);

        if (payload.getName() != null && !payload.getName().isBlank()) {
            existing.setName(payload.getName());
        }

        if (payload.getDescription() != null && !payload.getDescription().isBlank()) {
            existing.setDescription(payload.getDescription());
        }

        return badgeRepository.save(existing);
    }

    public void atualizar_status(Long id) {
        Badge badge = buscarPorId(id);
        badge.setAtivo(!Boolean.TRUE.equals(badge.getAtivo()));
        badgeRepository.save(badge);
    }

    public void deletar(Long id) {
        Badge badge = buscarPorId(id);
        badge.setAtivo(false);
        badgeRepository.save(badge);
    }

    private void validarBadge(Badge badge, Long id) {
        if (badge == null) {
            throw new RuntimeException("Badge é obrigatório");
        }

        if (badge.getName() == null || badge.getName().isBlank()) {
            throw new RuntimeException("Nome do badge é obrigatório");
        }

        if (badge.getDescription() == null || badge.getDescription().isBlank()) {
            throw new RuntimeException("Descrição do badge é obrigatória");
        }

        if (id == null) {
            if (badgeRepository.existsByNameIgnoreCase(badge.getName())) {
                throw new RuntimeException("Nome de badge já usado");
            }
        } else if (badgeRepository.existsByNameIgnoreCaseAndIdNot(badge.getName(), id)) {
            throw new RuntimeException("Nome de badge já usado");
        }
    }
}