package com.techub.api.seed;

import com.techub.api.domain.University;
import com.techub.api.repository.UniversityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class UniversitySeedRunner implements CommandLineRunner {

    private final UniversityRepository universityRepository;

    public UniversitySeedRunner(UniversityRepository universityRepository) {
        this.universityRepository = universityRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        String name = "Fatec Itaquera";
        if (!universityRepository.existsByNameIgnoreCase(name)) {
            University u = new University();
            u.setName(name);
            universityRepository.save(u);
        }
    }
}
