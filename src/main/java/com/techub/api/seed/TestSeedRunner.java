package com.techub.api.seed;

import com.techub.api.domain.*;
import com.techub.api.repository.*;
import com.techub.api.service.FollowService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(10)
public class TestSeedRunner implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final FollowService followService;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final AvatarRepository avatarRepository;
    private final FollowersRepository followersRepository;
    private final SummaryRepository summaryRepository;
    private final SubjectRepository subjectRepository;
    private final LikesRepository likesRepository;
    private final BadgeRepository badgeRepository;

    public TestSeedRunner(
            PasswordEncoder passwordEncoder,
            FollowService followService,
            UserRepository userRepository,
            StudentRepository studentRepository,
            CourseRepository courseRepository,
            AvatarRepository avatarRepository,
            FollowersRepository followersRepository,
            SummaryRepository summaryRepository,
            SubjectRepository subjectRepository,
            LikesRepository likesRepository,
            BadgeRepository badgeRepository
    ) {
        this.passwordEncoder = passwordEncoder;
        this.followService = followService;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.avatarRepository = avatarRepository;
        this.followersRepository = followersRepository;
        this.summaryRepository = summaryRepository;
        this.subjectRepository = subjectRepository;
        this.likesRepository = likesRepository;
        this.badgeRepository = badgeRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Course dsm = courseRepository.findTopByOrderByIdAsc().orElse(null);
        if (dsm == null) {
            System.err.println("[TestSeedRunner] Nenhum curso encontrado — seed abortado. Execute o CourseSeedRunner primeiro.");
            return;
        }

        List<Subject> subjects = subjectRepository.findAll();
        if (subjects.isEmpty()) {
            System.err.println("[TestSeedRunner] Nenhuma matéria encontrada — seed abortado. Execute o SubjectSeedRunner primeiro.");
            return;
        }

        Avatar defaultAvatar = avatarRepository.findByUrl("/avatares/default.svg").orElse(null);

        List<Student> students = new ArrayList<>();
        students.add(maybeCreateStudent("Lucas Mendes",      "lucas.mendes@fatec.sp.gov.br",      "senha123", 3, dsm, subjects, defaultAvatar, "/avatares/male-1.svg"));
        students.add(maybeCreateStudent("Ana Beatriz Costa", "ana.costa@fatec.sp.gov.br",         "senha123", 3, dsm, subjects, defaultAvatar, "/avatares/female-1.svg"));
        students.add(maybeCreateStudent("Pedro Alves",       "pedro.alves@fatec.sp.gov.br",       "senha123", 2, dsm, subjects, defaultAvatar, "/avatares/male-2.svg"));
        students.add(maybeCreateStudent("Mariana Oliveira",  "mariana.oliveira@fatec.sp.gov.br",  "senha123", 4, dsm, subjects, defaultAvatar, "/avatares/female-2.svg"));
        students.add(maybeCreateStudent("Rafael Souza",      "rafael.souza@fatec.sp.gov.br",      "senha123", 1, dsm, subjects, defaultAvatar, "/avatares/male-3.svg"));
        students.add(maybeCreateStudent("Camila Ferreira",   "camila.ferreira@fatec.sp.gov.br",   "senha123", 2, dsm, subjects, defaultAvatar, "/avatares/female-3.svg"));
        students.add(maybeCreateStudent("Bruno Carvalho",    "bruno.carvalho@fatec.sp.gov.br",    "senha123", 5, dsm, subjects, defaultAvatar, "/avatares/male-4.svg"));
        students.add(maybeCreateStudent("Julia Martins",     "julia.martins@fatec.sp.gov.br",     "senha123", 4, dsm, subjects, defaultAvatar, "/avatares/female-4.svg"));
        students.add(maybeCreateStudent("Diego Lima",        "diego.lima@fatec.sp.gov.br",        "senha123", 6, dsm, subjects, defaultAvatar, "/avatares/male-5.svg"));
        students.add(maybeCreateStudent("Fernanda Rocha",    "fernanda.rocha@fatec.sp.gov.br",    "senha123", 2, dsm, subjects, defaultAvatar, "/avatares/female-5.svg"));

        List<Student> novosAlunos = students.stream().filter(s -> s != null).toList();

        List<Professor> professors = new ArrayList<>();
        professors.add(maybeCreateProfessor("Prof. Carlos Eduardo",  "carlos.eduardo@fatec.sp.gov.br",  "prof123", subjects, defaultAvatar, "/avatares/male-1.svg",   0));
        professors.add(maybeCreateProfessor("Profa. Renata Silveira", "renata.silveira@fatec.sp.gov.br", "prof123", subjects, defaultAvatar, "/avatares/female-1.svg",  1));
        professors.add(maybeCreateProfessor("Prof. Marcelo Andrade",  "marcelo.andrade@fatec.sp.gov.br", "prof123", subjects, defaultAvatar, "/avatares/male-2.svg",   2));

        List<Professor> novosProfessores = professors.stream().filter(p -> p != null).toList();

        setupFollows(novosAlunos);

        if (!novosAlunos.isEmpty()) {
            setupLikes(novosAlunos);
        }


        if (!novosProfessores.isEmpty()) {
            setupBadges();
        }

        System.out.println("[TestSeedRunner] Seed concluído. "
                + novosAlunos.size() + " aluno(s) e "
                + novosProfessores.size() + " professor(es) criado(s).");
    }

    private Student maybeCreateStudent(
            String nome,
            String email,
            String senha,
            Integer semestre,
            Course course,
            List<Subject> allSubjects,
            Avatar defaultAvatar,
            String preferredAvatarUrl
    ) {
        if (userRepository.existsByEmail(email)) {
            System.out.println("[TestSeedRunner] Aluno já existe, ignorando: " + email);
            return null;
        }

        Avatar avatar = avatarRepository.findByUrl(preferredAvatarUrl)
                .or(() -> avatarRepository.findByUrl("/avatares/default.svg"))
                .orElse(defaultAvatar);

        Student student = new Student();
        student.setNome(nome);
        student.setSemestre(semestre);
        student.setCourse(course);
        student.setAtivo(true);
        student.setPontuacao(0);
        student.setAvatar(avatar);
        Student savedStudent = studentRepository.save(student);

        User user = new User();
        user.setEmail(email);
        user.setSenha(passwordEncoder.encode(senha));
        user.setRole(Role.ALUNO);
        user.setAtivo(true);
        user.setStudent(savedStudent);
        userRepository.save(user);

        criarResumos(savedStudent, semestre, allSubjects);

        return savedStudent;
    }

    private Professor maybeCreateProfessor(
            String nome,
            String email,
            String senha,
            List<Subject> allSubjects,
            Avatar defaultAvatar,
            String preferredAvatarUrl,
            int subjectIndex
    ) {
        if (userRepository.existsByEmail(email)) {
            System.out.println("[TestSeedRunner] Professor já existe, ignorando: " + email);
            return null;
        }

        Avatar avatar = avatarRepository.findByUrl(preferredAvatarUrl)
                .or(() -> avatarRepository.findByUrl("/avatares/default.svg"))
                .orElse(defaultAvatar);

        Subject subject = allSubjects.isEmpty()
                ? null
                : allSubjects.get(subjectIndex % allSubjects.size());

        Professor professor = new Professor();
        professor.setNome(nome);
        professor.setBio("Professor de " + (subject != null ? subject.getName() : "DSM") + " na Fatec.");
        professor.setAvatar(avatar);
        professor.setSubject(subject);
        professor.setAtivo(true);

        User user = new User();
        user.setEmail(email);
        user.setSenha(passwordEncoder.encode(senha));
        user.setRole(Role.PROFESSOR);
        user.setAtivo(true);
        user.setProfessor(professor);
        userRepository.save(user);

        return professor;
    }

    private void criarResumos(Student student, Integer semestre, List<Subject> allSubjects) {
        List<Subject> disponiveis = allSubjects.stream()
                .filter(s -> s.getSemestre() != null && s.getSemestre() <= semestre)
                .toList();

        if (disponiveis.isEmpty()) {
            disponiveis = allSubjects.stream().limit(3).toList();
        }
        if (disponiveis.isEmpty()) return;

        int total  = RESUMOS.length;
        int offset = Math.abs(student.getNome().hashCode()) % total;

        for (int i = 0; i < RESUMOS_POR_ALUNO; i++) {
            String[] dados = RESUMOS[(offset + i) % total];
            Subject materia = disponiveis.get(i % disponiveis.size());

            Summary summary = new Summary();
            summary.setTitulo(dados[0]);
            summary.setConteudo(dados[1]);
            summary.setStudent(student);
            summary.setSubject(materia);
            summary.setDatahora(LocalDateTime.now().minusDays(i * 3L));
            summary.setPublico(true);
            summary.setReports(0);
            summary.setAtivo(true);

            summaryRepository.save(summary);
        }
    }

    private void setupLikes(List<Student> students) {
        List<Summary> todosSumarios = summaryRepository.findByPublicoTrue();
        if (todosSumarios.isEmpty()) return;

        int likesAdicionados = 0;

        for (Student liker : students) {
            int curtidas = 0;
            for (Summary summary : todosSumarios) {
                if (curtidas >= 4) break;
                if (summary.getStudent() == null) continue;
                if (summary.getStudent().getId().equals(liker.getId())) continue;

                boolean jaExiste = likesRepository
                        .findByStudentAndSummary(liker, summary)
                        .isPresent();

                if (!jaExiste) {
                    Likes like = new Likes();
                    like.setStudent(liker);
                    like.setSummary(summary);
                    like.setAtivo(true);
                    likesRepository.save(like);
                    curtidas++;
                    likesAdicionados++;
                }
            }
        }

        System.out.println("[TestSeedRunner] " + likesAdicionados + " like(s) criado(s).");
    }

    private void boostSummariesTo50Likes(List<Student> baseStudents) {
        List<Summary> summaries = summaryRepository.findByPublicoTrue();
        if (summaries.isEmpty()) return;

        List<Student> likersPool = new ArrayList<>(baseStudents);

        // garante pelo menos 60 "likers" virtuais
        for (int i = 0; i < 60; i++) {
            Student bot = new Student();
            bot.setId(-1000L - i);
            bot.setNome("Bot User " + i);
            likersPool.add(bot);
        }

        for (Summary summary : summaries) {

            int currentLikes = (int) likesRepository.countBySummary(summary);

            if (currentLikes >= 50) continue;

            int needed = 50 - currentLikes;

            int added = 0;

            for (Student liker : likersPool) {

                if (added >= needed) break;

                if (summary.getStudent() != null &&
                        summary.getStudent().getId().equals(liker.getId())) {
                    continue;
                }

                boolean exists = likesRepository
                        .findByStudentAndSummary(liker, summary)
                        .isPresent();

                if (exists) continue;

                Likes like = new Likes();
                like.setStudent(liker);
                like.setSummary(summary);
                like.setAtivo(true);

                likesRepository.save(like);
                added++;
            }

            System.out.println("[Seed] Summary " + summary.getId() +
                    " agora tem +" + added + " likes (target 50)");
        }
    }

    private void setupBadges() {
        Badge badge = badgeRepository.findByNameIgnoreCaseAndAtivoTrue("Destaque do Professor")
                .orElseGet(() -> {
                    Badge novo = new Badge();
                    novo.setName("Destaque do Professor");
                    novo.setDescription("Concedido por um professor a um resumo de destaque");
                    novo.setAtivo(true);
                    return badgeRepository.save(novo);
                });

        List<Object[]> ranking = likesRepository.findRanking();

        int badgesAtribuidos = 0;
        for (Object[] row : ranking) {
            if (badgesAtribuidos >= 3) break;

            Long summaryId = ((Number) row[0]).longValue();
            summaryRepository.findById(summaryId).ifPresent(summary -> {
                if (summary.getBadge() == null) {
                    summary.setBadge(badge);
                    summaryRepository.save(summary);
                }
            });
            badgesAtribuidos++;
        }

        System.out.println("[TestSeedRunner] " + badgesAtribuidos + " badge(s) de destaque atribuído(s).");
    }

    private void setupFollows(List<Student> students) {
        for (Student follower : students) {
            for (Student following : students) {
                if (!follower.getId().equals(following.getId())) {
                    try {
                        followService.follow(follower.getId(), following.getId());
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    private static final int RESUMOS_POR_ALUNO = 8;

    private static final String[][] RESUMOS = {
            {
                    "Herança vs Composição em Java",
                    "Herança permite que uma classe filha reutilize atributos e métodos de uma classe pai via `extends`, mas cria acoplamento forte: mudanças na classe pai afetam todas as filhas. Composição é mais flexível — a classe contém uma instância de outra em vez de estendê-la. Regra prática: prefira composição quando a relação é 'tem um'; use herança só quando a relação for genuinamente 'é um'. Em Java, como não existe herança múltipla, composição + interfaces resolvem a maioria dos casos sem os problemas do acoplamento profundo."
            },
            {
                    "Normalização de Banco de Dados: 1FN, 2FN e 3FN",
                    "1FN: cada célula deve conter um valor atômico — nada de listas ou grupos repetidos na mesma coluna. 2FN: todos os atributos não-chave devem depender da chave primária inteira (elimina dependências parciais em chaves compostas). 3FN: nenhum atributo não-chave deve depender de outro atributo não-chave (elimina dependências transitivas). Normalizar reduz redundância e anomalias de inserção/atualização/exclusão. Para OLAP e relatórios, desnormalizar controladamente pode ser justificado por performance."
            },
            {
                    "Complexidade de Algoritmos: Notação Big O",
                    "Big O descreve como o tempo (ou espaço) cresce em relação ao tamanho n da entrada, no pior caso. O(1) = constante; O(log n) = busca binária; O(n) = percurso linear; O(n log n) = merge sort; O(n²) = bubble sort / loops aninhados; O(2ⁿ) = força bruta em subconjuntos. Para comparar algoritmos, ignora-se a constante multiplicativa — O(100n) e O(n) são equivalentes assintoticamente. Otimizar de O(n²) para O(n log n) tem impacto real com n > 10.000."
            },
            {
                    "TCP vs UDP: quando usar cada um",
                    "TCP garante entrega ordenada e sem perdas via handshake de 3 vias, ACKs e retransmissão. Tem overhead maior, mas é ideal para HTTP, HTTPS, transferência de arquivos e emails. UDP é connectionless, sem garantia de entrega ou ordem — mas tem latência baixíssima. Ideal para streaming de vídeo/áudio, jogos online e DNS (onde perder um pacote é menos grave do que atrasar). WebRTC usa UDP por baixo. QUIC (base do HTTP/3) é UDP com confiabilidade implementada na camada de aplicação."
            },
            {
                    "REST: os 6 constraints que definem a arquitetura",
                    "REST não é apenas 'usar HTTP com JSON'. Os 6 constraints de Fielding são: 1) Client-Server: separação de responsabilidades. 2) Stateless: cada requisição carrega todo o contexto necessário. 3) Cache: respostas devem indicar se são cacheáveis. 4) Interface Uniforme: URIs identificam recursos, HTTP verbs definem ações. 5) Layered System: cliente não sabe se fala com servidor final ou proxy. 6) Code on Demand (opcional): servidor pode enviar código executável. APIs que seguem esses princípios são chamadas RESTful."
            },
            {
                    "Injeção de Dependência no Spring Boot",
                    "O Spring IoC Container gerencia o ciclo de vida dos beans. @Component, @Service, @Repository e @Controller marcam classes para serem gerenciadas. @Autowired injeta dependências — mas injeção por construtor é preferível à injeção por campo porque torna dependências explícitas e facilita testes. @Bean em classes @Configuration permite declarar beans de libs externas. @Scope controla se o bean é singleton (padrão), prototype, request ou session. Entender o contexto de aplicação evita NullPointerException e circular dependency."
            },
            {
                    "Docker: Imagem, Container e Dockerfile",
                    "Imagem é o template imutável (filesystem + metadados) gerado pelo `docker build`. Container é uma instância em execução da imagem — isolada via namespaces e cgroups do Linux. Dockerfile define as camadas: FROM (base), COPY (arquivos), RUN (comandos), EXPOSE (porta), CMD/ENTRYPOINT (comando padrão). Cada instrução gera uma camada cacheável. Para reduzir tamanho da imagem: use multi-stage builds (compilar em uma imagem, copiar apenas o artefato para outra menor). `docker-compose` orquestra múltiplos containers localmente."
            },
            {
                    "Git: Merge vs Rebase — qual usar?",
                    "Merge cria um commit de merge preservando o histórico exato de ambas as branches. Rebase reescreve o histórico aplicando seus commits sobre a ponta da branch alvo — histórico linear, mas commits têm novo hash. Regra: nunca faça rebase de branches públicas/compartilhadas (reescreve histórico que outros têm). Use rebase para limpar commits locais antes de abrir PR. Use merge para integrar branches de feature em main em projetos de time. `git rebase -i` (interativo) permite squash, reorder e reword de commits."
            },
            {
                    "Hooks no React: useState e useEffect",
                    "useState gerencia estado local: `const [valor, setValor] = useState(inicial)`. A cada chamada de setter, o componente re-renderiza. useEffect executa efeitos colaterais: chamadas API, subscriptions, manipulação do DOM. O array de dependências controla quando roda — `[]` roda só na montagem, `[x]` roda quando x muda, sem array roda em todo render. Retornar uma função no useEffect é o cleanup (cancela subscriptions, timers). No React 18 com StrictMode, useEffect roda duas vezes em dev para detectar efeitos não-idempotentes."
            },
            {
                    "TypeScript: tipos utilitários essenciais",
                    "Partial<T> torna todos os campos opcionais — útil para funções de update. Required<T> faz o oposto. Pick<T, K> extrai apenas as chaves K de T. Omit<T, K> remove as chaves K. Readonly<T> impede mutação. Record<K, V> cria um objeto com chaves K e valores V. ReturnType<F> extrai o tipo de retorno de uma função. Parameters<F> extrai os tipos dos parâmetros. Esses tipos utilitários eliminam duplicação de interfaces e mantêm o código tipado sem repetição. Combine com generics para máxima reutilização."
            },
            {
                    "Padrão Repository e separação de camadas",
                    "O padrão Repository abstrai o acesso a dados atrás de uma interface, desacoplando a lógica de negócio da infraestrutura de persistência. Na prática com Spring Data JPA: crie uma interface que estende JpaRepository<Entidade, ID> e ganhe CRUD grátis. Métodos derivados por convenção de nome (findByEmailAndAtivo) são gerados automaticamente. Para queries complexas, use @Query com JPQL ou SQL nativo. Nunca coloque lógica de negócio no repository — isso vai na Service. Controllers não devem chamar repositories diretamente."
            },
            {
                    "Scrum: cerimônias e papéis",
                    "Scrum define 3 papéis: Product Owner (PO) — dono do backlog e das prioridades; Scrum Master — facilita o processo e remove impedimentos; Dev Team — auto-organizado e responsável pela entrega. As 4 cerimônias são: Sprint Planning (planejar o que entra na sprint), Daily Scrum (15min de sincronização diária), Sprint Review (demonstrar o incremento ao PO) e Sprint Retrospective (o time melhora o processo). Sprint tem duração fixa (1 a 4 semanas). Definition of Done define quando uma história está realmente concluída."
            },
            {
                    "Programação Assíncrona: Promises e async/await",
                    "Promise representa um valor que estará disponível no futuro — pode estar pending, fulfilled ou rejected. `.then()` encadeia sucesso, `.catch()` captura erro, `.finally()` sempre roda. `async/await` é açúcar sintático sobre Promises: `await` pausa a execução da função async até a Promise resolver. Sempre use try/catch com await para tratar erros. `Promise.all([p1, p2])` paraleliza e falha rápido se uma rejeitar. `Promise.allSettled` espera todas independente de falha. Evite `await` dentro de `.forEach` — prefira `Promise.all` + `.map`."
            },
            {
                    "Índices em banco de dados: quando e por quê",
                    "Índice é uma estrutura auxiliar (geralmente B-tree) que acelera buscas em troca de espaço e overhead em writes. Crie índices em colunas usadas frequentemente em WHERE, JOIN, ORDER BY. Foreign keys devem ter índice. Índice único garante unicidade e acelera buscas por email/CPF. Muitos índices em tabelas com alto volume de INSERT/UPDATE/DELETE degradam performance. Use EXPLAIN/EXPLAIN ANALYZE para ver se a query usa o índice. Índice composto (col1, col2) só funciona com prefixo: filtra por col1 ou (col1 + col2), mas não só por col2."
            },
    };
}