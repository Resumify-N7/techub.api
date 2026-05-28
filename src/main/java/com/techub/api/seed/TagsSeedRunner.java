package com.techub.api.seed;

import com.techub.api.domain.Tags;
import com.techub.api.repository.TagsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TagsSeedRunner implements CommandLineRunner {

    private final TagsRepository tagsRepository;

    public TagsSeedRunner(TagsRepository tagsRepository) {
        this.tagsRepository = tagsRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<String> tags = List.of(
                "Java",
                "Spring Boot",
                "Banco de Dados",
                "JavaScript",
                "TypeScript",
                "Python",
                "Algoritmos",
                "Estruturas de Dados",
                "Engenharia de Software",
                "Redes de Computadores"
        );

        for (String nome : tags) {
            if (!tagsRepository.existsByNameIgnoreCase(nome)) {
                Tags tag = new Tags();
                tag.setName(nome);
                tag.setAtivo(true);
                tagsRepository.save(tag);
            }
        }
    }
}
