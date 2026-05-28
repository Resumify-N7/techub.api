package com.techub.api.seed;

import com.techub.api.domain.Badge;
import com.techub.api.repository.BadgeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BadgeSeedRunner implements CommandLineRunner {

    private final BadgeRepository badgeRepository;

    public BadgeSeedRunner(BadgeRepository badgeRepository) {
        this.badgeRepository = badgeRepository;
    }

    @Override
    public void run(String... args) {
        String nomeBadge = "Resumo Popular";

        if (!badgeRepository.existsByNameIgnoreCase(nomeBadge)) {
            Badge badge = new Badge();
            badge.setName(nomeBadge);
            badge.setDescription("Concedido ao autor de um resumo que alcançar 50 curtidas");
            badge.setAtivo(true);
            badgeRepository.save(badge);
        }
    }
}