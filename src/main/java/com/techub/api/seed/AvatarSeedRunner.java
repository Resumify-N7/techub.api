package com.techub.api.seed;

import com.techub.api.domain.Avatar;
import com.techub.api.repository.AvatarRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile({"test","seed"})
public class AvatarSeedRunner implements CommandLineRunner {

    private final AvatarRepository avatarRepository;

    public AvatarSeedRunner(AvatarRepository avatarRepository) {
        this.avatarRepository = avatarRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        List<String> urls = new ArrayList<>();
        urls.add("/avatares/default.svg");

        for (int i = 1; i <= 13; i++) {
            urls.add("/avatares/female-" + i + ".svg");
        }

        for (int i = 1; i <= 8; i++) {
            urls.add("/avatares/male-" + i + ".svg");
        }

        for (String url : urls) {
            avatarRepository.findByUrl(url).ifPresentOrElse(a -> {
                // already exists - skip
            }, () -> {
                Avatar a = new Avatar();
                a.setUrl(url);
                String title = url.replaceAll("\\.svg$", "");
                a.setTitle(title);
                if (url.startsWith("female-")) {
                    a.setMale(false);
                } else if (url.startsWith("male-")) {
                    a.setMale(true);
                } else {
                    a.setMale(null);
                }
                a.setDescription("Seeded avatar: " + title);
                avatarRepository.save(a);
            });
        }
    }
}
