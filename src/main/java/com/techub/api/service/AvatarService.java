package com.techub.api.service;

import com.techub.api.domain.Avatar;
import com.techub.api.repository.AvatarRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvatarService {

    private final AvatarRepository avatarRepository;

    public AvatarService(AvatarRepository avatarRepository) {
        this.avatarRepository = avatarRepository;
    }

    public List<Avatar> listar(Boolean male) {
        if (male == null) {
            return avatarRepository.findAllByOrderByIdAsc();
        }

        return male
                ? avatarRepository.findByMaleTrueOrderByIdAsc()
                : avatarRepository.findByMaleFalseOrderByIdAsc();
    }

    public Avatar buscarPorId(Long id) {
        return avatarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avatar não encontrado"));
    }

    public Avatar buscarPorUrl(String url) {
        return avatarRepository.findByUrl(url)
                .orElseThrow(() -> new RuntimeException("Avatar não encontrado"));
    }

    public Avatar getOrCreateDefault() {
        String defaultUrl = "/avatares/default.svg";
        return avatarRepository.findByUrl(defaultUrl).orElseGet(() -> {
            Avatar a = new Avatar();
            a.setTitle("default");
            a.setMale(null);
            a.setUrl(defaultUrl);
            a.setDescription("Avatar padrão");
            return avatarRepository.save(a);
        });
    }
}