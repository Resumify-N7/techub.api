package com.techub.api.seed;

import com.techub.api.domain.Tags;
import com.techub.api.repository.TagsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TagsSeedRunner implements CommandLineRunner {

    private final TagsRepository tagsRepository;

    public TagsSeedRunner(TagsRepository tagsRepository) {
        this.tagsRepository = tagsRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        String[] tags = {"Java", "Spring", "Banco de Dados"};
        for (String t : tags) {
            if (!tagsRepository.existsByNameIgnoreCase(t)) {
                Tags tag = new Tags();
                tag.setName(t);
                tagsRepository.save(tag);
            }
        }
    }
}
