package com.techub.api.seed;

import com.techub.api.domain.Course;
import com.techub.api.domain.Subject;
import com.techub.api.repository.CourseRepository;
import com.techub.api.repository.SubjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SubjectSeedRunner implements CommandLineRunner {

    private final SubjectRepository subjectRepository;
    private final CourseRepository courseRepository;

    public SubjectSeedRunner(SubjectRepository subjectRepository, CourseRepository courseRepository) {
        this.subjectRepository = subjectRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Course dsmCourse = courseRepository.findTopByOrderByIdAsc()
                .orElse(null);

        if (dsmCourse == null) {
            return;
        }

        createSubjectIfNotExists("Engenharia de Software I", 1, dsmCourse);
        createSubjectIfNotExists("Sistemas Operacionais de Redes de Computadores", 1, dsmCourse);
        createSubjectIfNotExists("Algoritmos e Lógica de Programação", 1, dsmCourse);
        createSubjectIfNotExists("Desenvolvimento Web I", 1, dsmCourse);
        createSubjectIfNotExists("Design Digital", 1, dsmCourse);
        createSubjectIfNotExists("Modelagem de Banco de Dados", 1, dsmCourse);

        createSubjectIfNotExists("Engenharia de Software II", 2, dsmCourse);
        createSubjectIfNotExists("Matemática para Computação", 2, dsmCourse);
        createSubjectIfNotExists("Desenvolvimento Web II", 2, dsmCourse);
        createSubjectIfNotExists("Banco de Dados – Relacional", 2, dsmCourse);
        createSubjectIfNotExists("Estrutura de Dados", 2, dsmCourse);
        createSubjectIfNotExists("Técnica de Programação I", 2, dsmCourse);

        createSubjectIfNotExists("Desenvolvimento Web III", 3, dsmCourse);
        createSubjectIfNotExists("Técnica de Programação II", 3, dsmCourse);
        createSubjectIfNotExists("Álgebra Linear", 3, dsmCourse);
        createSubjectIfNotExists("Banco de Dados – Não Relacional", 3, dsmCourse);
        createSubjectIfNotExists("Gestão Ágil de Projetos de Software", 3, dsmCourse);
        createSubjectIfNotExists("Inglês I", 3, dsmCourse);
        createSubjectIfNotExists("Interação Humano Computador", 3, dsmCourse);

        createSubjectIfNotExists("Experiência do Usuário", 4, dsmCourse);
        createSubjectIfNotExists("Programação para Dispositivos Móveis I", 4, dsmCourse);
        createSubjectIfNotExists("Estatística Aplicada", 4, dsmCourse);
        createSubjectIfNotExists("Internet das Coisas e Aplicações", 4, dsmCourse);
        createSubjectIfNotExists("Integração e Entrega Contínua", 4, dsmCourse);
        createSubjectIfNotExists("Laboratório de Desenvolvimento Web", 4, dsmCourse);
        createSubjectIfNotExists("Inglês II", 4, dsmCourse);

        createSubjectIfNotExists("Programação para Dispositivos Móveis II", 5, dsmCourse);
        createSubjectIfNotExists("Aprendizagem de Máquina", 5, dsmCourse);
        createSubjectIfNotExists("Laboratório de Desenvolvimento Para Dispositivos Móveis", 5, dsmCourse);
        createSubjectIfNotExists("Segurança no Desenvolvimento de Aplicações", 5, dsmCourse);
        createSubjectIfNotExists("Computação em Nuvem I", 5, dsmCourse);
        createSubjectIfNotExists("Fundamentos da Redação Técnica", 5, dsmCourse);
        createSubjectIfNotExists("Inglês III", 5, dsmCourse);

        createSubjectIfNotExists("Processamento de Linguagem Natural", 6, dsmCourse);
        createSubjectIfNotExists("Qualidade e Testes de Software", 6, dsmCourse);
        createSubjectIfNotExists("Computação Em Nuvem II", 6, dsmCourse);
        createSubjectIfNotExists("Mineração de Dados", 6, dsmCourse);
        createSubjectIfNotExists("Laboratório de Desenvolvimento Multiplataforma", 6, dsmCourse);
        createSubjectIfNotExists("Ética Profissional e Patente", 6, dsmCourse);
        createSubjectIfNotExists("Inglês IV", 6, dsmCourse);
    }

    private void createSubjectIfNotExists(String name, Integer semestre, Course course) {
        if (!subjectRepository.existsByNameIgnoreCase(name)) {
            Subject subject = new Subject();
            subject.setName(name);
            subject.setSemestre(semestre);
            subject.setCourse(course);
            subjectRepository.save(subject);
        }
    }
}
