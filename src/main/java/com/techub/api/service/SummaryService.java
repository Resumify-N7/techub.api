package com.techub.api.service;

import com.techub.api.domain.*;
import com.techub.api.dto.*;
import com.techub.api.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class SummaryService {

    private final SummaryRepository summaryRepository;
    private final LikesService likesService;
    private final ReportRepository reportRepository;
    private final TagsRepository tagsRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final StudentService studentService;
    private final CurrentUserService currentUserService;
    private final BadgeRepository badgeRepository;

    public SummaryService(
            SummaryRepository summaryRepository,
            LikesService likesService,
            ReportRepository reportRepository,
            TagsRepository tagsRepository,
            StudentRepository studentRepository,
            SubjectRepository subjectRepository,
            StudentService studentService,
            CurrentUserService currentUserService,
            BadgeRepository badgeRepository
    ) {
        this.summaryRepository = summaryRepository;
        this.likesService = likesService;
        this.reportRepository = reportRepository;
        this.tagsRepository = tagsRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.studentService = studentService;
        this.currentUserService = currentUserService;
        this.badgeRepository = badgeRepository;
    }

    @Transactional
    public SummaryCreateResponseDTO saveSummary(SummaryCreateRequestDTO dto, Long id) {
        if (dto.titulo() == null || dto.titulo().isEmpty())
            throw new IllegalArgumentException("Título é obrigatório");
        if (dto.conteudo() == null || dto.conteudo().isEmpty())
            throw new IllegalArgumentException("Conteudo é obrigatório");
        if (dto.subjectId() == null)
            throw new IllegalArgumentException("Matéria é obrigatória");

        Student student = studentService.resolveStudentByIdOrUserId(id);
        Subject subject = subjectRepository.findById(dto.subjectId())
                .orElseThrow(() -> new RuntimeException("Matéria não encontrada"));

        if (student.getCourse() == null || subject.getCourse() == null
                || !student.getCourse().getId().equals(subject.getCourse().getId())) {
            throw new RuntimeException("A matéria precisa pertencer ao curso do estudante");
        }

        if (student.getSemestre() != null && subject.getSemestre() != null
                && !student.getSemestre().equals(subject.getSemestre())) {
            throw new RuntimeException("A matéria precisa pertencer ao semestre atual do estudante");
        }

        Summary summary = new Summary();
        summary.setTitulo(dto.titulo());
        summary.setConteudo(dto.conteudo());
        summary.setStudent(student);
        summary.setSubject(subject);
        summary.setDatahora(LocalDateTime.now());
        summary.setPublico(dto.publico() == null ? Boolean.TRUE : dto.publico());

        if (dto.tagsIds() != null && !dto.tagsIds().isEmpty()) {
            var tagsById = tagsRepository.findAllById(dto.tagsIds())
                    .stream()
                    .collect(java.util.stream.Collectors.toMap(Tags::getId, tag -> tag));

            for (Long tagId : dto.tagsIds()) {
                Tags tag = tagsById.get(tagId);
                if (tag == null) throw new RuntimeException("Tag não encontrada: " + tagId);
                if (!Boolean.TRUE.equals(tag.getAtivo())) throw new RuntimeException("Tag inativa: " + tagId);

                TagSummary link = new TagSummary();
                link.setSummary(summary);
                link.setTag(tag);
                summary.getTagLinks().add(link);
            }
        }

        summaryRepository.save(summary);
        return new SummaryCreateResponseDTO(
                summary.getId(), student.getId(), subject.getId(),
                summary.getTitulo(), summary.getConteudo()
        );
    }
    @Transactional(readOnly = true)
    public List<SummaryGetResponseDTO> getAll(int limit) {
        return summaryRepository.findActive(PageRequest.of(0, Math.max(1, limit)))
                .getContent().stream().map(s -> toResponse(s, true)).toList();
    }

    @Transactional(readOnly = true)
    public FeedDTO findByAtivoTruePaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, Math.max(1, size));
        Page<Summary> result = summaryRepository.findActivePublicOrderedByDate(pageable);

        return new FeedDTO(
                result.getContent().stream().map(s -> toResponse(s, true)).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements()
        );
    }

    @Transactional(readOnly = true)
    public FeedDTO getRankingPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, Math.max(1, size));
        Page<Summary> result = summaryRepository.findRanking(pageable);

        return new FeedDTO(
                result.getContent().stream().map(s -> toResponse(s, true)).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements()
        );
    }

    @Transactional(readOnly = true)
    public List<SummaryGetResponseDTO> findByAtivoFalse(int limit) {
        return summaryRepository.findInactive(PageRequest.of(0, Math.max(1, limit)))
                .stream().map(s -> toResponse(s, false)).toList();
    }

    @Transactional(readOnly = true)
    public SummaryGetResponseDTO getById(Long id) {
        Summary summary = summaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resumo não encontrado"));

        User currentUser = currentUserService.getCurrentUser();
        Student student = currentUser.getStudent();

        boolean isPrivate = Boolean.FALSE.equals(summary.getPublico());
        boolean isOwner   = student != null && summary.getStudent().getId().equals(student.getId());
        boolean isPrivileged = currentUser.getRole() == Role.ADM
                || currentUser.getRole() == Role.PROFESSOR;

        if (isPrivate && !isOwner && !isPrivileged) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Este resumo é privado");
        }
        if (Boolean.FALSE.equals(summary.getAtivo()) && !isPrivileged) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Este resumo está desativado");
        }

        return toResponse(summary, true);
    }

    @Transactional(readOnly = true)
    public SummaryGetResponseDTO getByIdAsAdmin(Long id) {
        Summary summary = summaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resumo não encontrado"));
        return toResponse(summary, true);
    }

    @Transactional(readOnly = true)
    public List<SummaryGetResponseDTO> getStudentSummary(Long id, int limit) {
        Student student = studentService.resolveStudentByIdOrUserId(id);
        return summaryRepository
                .findByStudentIdAndAtivoTrue(student.getId(), PageRequest.of(0, Math.max(1, limit)))
                .getContent().stream()
                .map(s -> toResponse(s, true))
                .filter(item -> Boolean.TRUE.equals(item.ativo()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SummaryGetResponseDTO> getStudentSummaryByStudentId(Long studentId, int limit) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estudante não encontrado"));
        return summaryRepository
                .findByStudentIdAndAtivoTrue(student.getId(), PageRequest.of(0, Math.max(1, limit)))
                .getContent().stream()
                .map(s -> toResponse(s, true))
                .filter(item -> Boolean.TRUE.equals(item.publico()))
                .toList();
    }

    public List<SummaryGetResponseDTO> getBySubjectId(Long subjectId, int limit) {
        return summaryRepository
                .findBySubjectIdAndAtivoTrue(subjectId, PageRequest.of(0, Math.max(1, limit)))
                .getContent().stream().map(s -> toResponse(s, true)).toList();
    }

    public SummaryGetResponseDTO update(Long id, SummaryUpdateRequestDTO dto) {
        Summary existing = summaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resumo não encontrado"));

        Subject subject = subjectRepository.findById(dto.summaryId())
                .orElseThrow(() -> new RuntimeException("Erro ao encontrar a materia"));

        existing.getTagLinks().clear();
        summaryRepository.saveAndFlush(existing);

        if (dto.tags() != null && !dto.tags().isEmpty()) {
            var tagsById = tagsRepository.findAllById(dto.tags())
                    .stream()
                    .collect(java.util.stream.Collectors.toMap(Tags::getId, tag -> tag));

            for (Long tagId : dto.tags()) {
                Tags tag = tagsById.get(tagId);
                if (tag == null) throw new RuntimeException("Tag não encontrada: " + tagId);
                if (!Boolean.TRUE.equals(tag.getAtivo())) throw new RuntimeException("Tag inativa: " + tagId);

                TagSummary link = new TagSummary();
                link.setSummary(existing);
                link.setTag(tag);
                existing.getTagLinks().add(link);
            }
        }

        existing.setTitulo(dto.titulo());
        existing.setConteudo(dto.conteudo());
        existing.setSubject(subject);
        existing.setPublico(dto.publico());

        summaryRepository.save(existing);
        return toResponse(existing, true);
    }

    public void reportar(Long id, Long studentId) {
        Summary summary = summaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o resumo!"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        if (reportRepository.existsByStudentAndSummary(student, summary))
            throw new RuntimeException("Você já reportou este resumo");

        Report report = new Report();
        report.setStudent(student);
        report.setSummary(summary);
        report.setReportado(true);
        reportRepository.save(report);
    }

    public void atualizar_status(Long id) {
        Summary summary = summaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o resumo!"));
        summary.setAtivo(!summary.getAtivo());
        summaryRepository.save(summary);
    }

    public void delete(Long id) {
        Summary existing = summaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resumo não encontrado"));
        existing.setAtivo(false);
        summaryRepository.save(existing);
    }

    public void alternarVisibilidade(Long summaryId, Long studentId) {
        Summary summary = summaryRepository.findById(summaryId)
                .orElseThrow(() -> new RuntimeException("Erro ao procurar resumo!"));
        if (!summary.getStudent().getId().equals(studentId))
            throw new RuntimeException("Você não tem permissão para alterar este resumo");
        summary.setPublico(!summary.getPublico());
        summaryRepository.save(summary);
    }

    @Transactional
    public void atribuirBadgeProfessor(Long summaryId) {
        Summary summary = summaryRepository.findById(summaryId)
                .orElseThrow(() -> new RuntimeException("Resumo não encontrado"));

        Badge badge = badgeRepository.findByNameIgnoreCaseAndAtivoTrue("Destaque do Professor")
                .orElseGet(() -> {
                    Badge novo = new Badge();
                    novo.setName("Destaque do Professor");
                    novo.setDescription("Concedido por um professor a um resumo de destaque");
                    novo.setAtivo(true);
                    return badgeRepository.save(novo);
                });

        summary.setBadge(badge);
        summaryRepository.save(summary);
    }

    @Transactional
    public void removerBadgeProfessor(Long summaryId) {
        Summary summary = summaryRepository.findById(summaryId)
                .orElseThrow(() -> new RuntimeException("Resumo não encontrado"));
        summary.setBadge(null);
        summaryRepository.save(summary);
    }

    private SummaryGetResponseDTO toResponse(Summary summary, boolean includeLikes) {
        Long totalCurtidas = null;
        Integer totalReports = Math.toIntExact(reportRepository.countBySummaryAndReportadoTrue(summary));

        if (includeLikes && Boolean.TRUE.equals(summary.getAtivo()) && Boolean.TRUE.equals(summary.getPublico())) {
            totalCurtidas = likesService.contarCurtidas(summary);
        }

        var tags = summary.getTagLinks().stream()
                .map(link -> {
                    var tag = link.getTag();
                    if (tag == null) return null;
                    return new TagResponseDTO(tag.getId(), tag.getName());
                })
                .filter(Objects::nonNull)
                .toList();

        String studentUrl = summary.getStudent().getAvatar() != null
                ? summary.getStudent().getAvatar().getUrl()
                : "/avatares/default.svg";

        return new SummaryGetResponseDTO(
                summary.getId(),
                summary.getStudent().getId(),
                summary.getStudent().getNome(),
                studentUrl,
                summary.getSubject() != null ? summary.getSubject().getId() : null,
                summary.getSubject() != null ? summary.getSubject().getName() : null,
                summary.getTitulo(),
                summary.getConteudo(),
                totalReports,
                summary.getPublico(),
                summary.getAtivo(),
                totalCurtidas,
                tags,
                summary.getBadge() != null
                        ? new SummaryGetResponseDTO.BadgeDTO(
                        summary.getBadge().getId(),
                        summary.getBadge().getName(),
                        summary.getBadge().getDescription())
                        : null
        );
    }
}