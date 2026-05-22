package com.techub.api.seed;

import com.techub.api.domain.Bio;
import com.techub.api.repository.BioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Profile({"test", "seed"})
public class BioSeedRunner implements CommandLineRunner {

    private final BioRepository bioRepository;

    public BioSeedRunner(BioRepository bioRepository) {
        this.bioRepository = bioRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<String> bios = List.of(
                "Sou uma pessoa criativa e introvertida. Gosto de tecnologia, música e passar horas aprendendo coisas novas. Às vezes demoro para responder, mas adoro conversar quando me sinto confortável.",
                "Tenho espírito curioso e adoro descobrir como as coisas funcionam. Passo boa parte do meu tempo entre livros, séries e projetos pessoais.",
                "Sou tranquilo, observador e gosto de ambientes leves. Prefiro conversas sinceras, café forte e boas ideias.",
                "Vivo entre estudos, playlists e muita vontade de aprender. Quando me empolgo com algo, mergulho de cabeça.",
                "Sou uma pessoa reservada, mas bastante leal. Demoro a abrir o coração, mas quando isso acontece sou presença de verdade.",
                "Gosto de tecnologia, criatividade e desafios que me tirem da zona de conforto. Aprender faz parte da minha rotina.",
                "Sou alguém que valoriza conexão, calmaria e autenticidade. Prefiro poucos bons amigos a muitas conversas rasas.",
                "Tenho uma mente inquieta e adoro explorar assuntos novos. Sempre estou buscando algo para estudar ou criar.",
                "Sou mais quieto no começo, mas costumo me soltar quando encontro confiança. No fundo, gosto mesmo é de boas conversas.",
                "Vivo tentando equilibrar rotina, sonhos e curiosidade. Música e tecnologia são meus refúgios favoritos.",
                "Sou uma pessoa observadora, sensível e criativa. Gosto de pensar bastante antes de agir, mas sem perder a leveza.",
                "Tenho interesse por aprendizado contínuo e gosto de ideias diferentes. Sempre tento transformar inspiração em ação.",
                "Sou de poucos exageros e muitos detalhes. Curto ambientes tranquilos, conversas honestas e gente de verdade.",
                "Tenho um lado artístico que aparece em tudo que faço. Gosto de criar, experimentar e deixar a imaginação guiar um pouco.",
                "Sou focado, mas também gosto de momentos simples. Uma boa música e um assunto interessante já melhoram meu dia.",
                "Gosto de entender o mundo com calma. Sou mais reflexivo do que impulsivo e isso faz parte de quem eu sou.",
                "Sou comunicativo quando me sinto à vontade e bastante dedicado com o que importa para mim. A conexão vem com o tempo.",
                "Tenho curiosidade por quase tudo. Tecnologia, cultura e aprendizado são temas que me prendem facilmente.",
                "Sou uma pessoa sensível e determinada. Gosto de evoluir sem pressa, respeitando meu próprio ritmo.",
                "Vivo entre ideias, playlists e planos futuros. Gosto de estudar, testar coisas novas e trocar conhecimento.",
                "Sou discreto no começo, mas bem intenso quando algo me interessa de verdade. Curto profundidade mais do que aparência.",
                "Tenho carinho por conversas leves e significativas ao mesmo tempo. Valorizo calma, respeito e autenticidade.",
                "Sou criativo, um pouco tímido e muito interessado em aprender. Quando me conecto com um assunto, fico horas nele.",
                "Gosto de observar antes de opinar. Sou alguém que prefere profundidade, boas ideias e momentos tranquilos."
        );

        for (String description : bios) {
            bioRepository.findByDescription(description).orElseGet(() -> {
                Bio bio = new Bio();
                bio.setDescription(description);
                return bioRepository.save(bio);
            });
        }
    }
}
