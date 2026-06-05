package com.techub.api.service;

import com.techub.api.domain.*;
import com.techub.api.dto.*;
import com.techub.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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

    public SummaryService(
            SummaryRepository summaryRepository,
            LikesService likesService,
            ReportRepository reportRepository,
            TagsRepository tagsRepository,
            StudentRepository studentRepository,
            SubjectRepository subjectRepository,
            StudentService studentService,
            CurrentUserService currentUserService
    ) {
        this.summaryRepository = summaryRepository;
        this.likesService = likesService;
        this.reportRepository = reportRepository;
        this.tagsRepository = tagsRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.studentService = studentService;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public SummaryCreateResponseDTO saveSummary(SummaryCreateRequestDTO dto, Long id) {

        if (dto.titulo() == null || dto.titulo().isEmpty()) {
            throw new IllegalArgumentException("Título é obrigatório");
        }

        if (dto.conteudo() == null || dto.conteudo().isEmpty()) {
            throw new IllegalArgumentException("Conteudo é obrigatório");
        }

        if (dto.subjectId() == null) {
            throw new IllegalArgumentException("Matéria é obrigatória");
        }

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
                if (tag == null) {
                    throw new RuntimeException("Tag não encontrada: " + tagId);
                }
                if (!Boolean.TRUE.equals(tag.getAtivo())) {
                    throw new RuntimeException("Tag inativa: " + tagId);
                }

                TagSummary link = new TagSummary();
                link.setSummary(summary);
                link.setTag(tag);
                summary.getTagLinks().add(link);
            }
        }

        try {
            summaryRepository.save(summary);
            return new SummaryCreateResponseDTO(
                    summary.getId(),
                    student.getId(),
                    subject.getId(),
                    summary.getTitulo(),
                    summary.getConteudo()
            );
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar resumo");
        }
    }

    private SummaryGetResponseDTO toResponse(Summary summary, boolean includeLikes) {
        Long totalCurtidas = null;

        Integer totalReports = Math.toIntExact(reportRepository.countBySummaryAndReportadoTrue(summary));

        if (includeLikes && Boolean.TRUE.equals(summary.getAtivo()) && Boolean.TRUE.equals(summary.getPublico())) {
            totalCurtidas = likesService.contarCurtidas(summary);
        }

        var tags = summary.getTagLinks()
                .stream()
                .map(link -> {
                    var tag = link.getTag();
                    if (tag == null) return null;

                    return new TagResponseDTO(
                        tag.getId(),
                        tag.getName()
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        return new SummaryGetResponseDTO(
                summary.getId(),
                summary.getStudent().getId(),
                summary.getStudent().getNome(),
                summary.getStudent().getAvatar().getUrl(),
                summary.getSubject() != null ? summary.getSubject().getId() : null,
                summary.getSubject() != null ? summary.getSubject().getName() : null,
                summary.getTitulo(),
                summary.getConteudo(),
                totalReports,
                summary.getPublico(),
                summary.getAtivo(),
                totalCurtidas,
                tags
        );
    }

    @Transactional(readOnly = true)
    public List<SummaryGetResponseDTO> getAll(int limit) {
        int pageSize = Math.max(1, limit);

        return summaryRepository.findActive(PageRequest.of(0, pageSize))
                .getContent()
                .stream()
                .map(summary -> toResponse(summary, true))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SummaryGetResponseDTO> findByAtivoTrue(int limit) {
        int pageSize = Math.max(1, limit);

        return summaryRepository.findActive(PageRequest.of(0, pageSize))
                .stream()
                .filter(summary -> summary.getPublico() == true)
            .map(summary -> toResponse(summary, true))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SummaryGetResponseDTO> findByAtivoFalse(int limit) {
        int pageSize = Math.max(1, limit);

        return summaryRepository.findInactive(PageRequest.of(0, pageSize))
                .stream()
            .map(summary -> toResponse(summary, false))
                .toList();
    }

    @Transactional(readOnly = true)
    public SummaryGetResponseDTO getById(Long id) {
        Summary summary = summaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resumo não encontrado"));

        Student student = currentUserService.getCurrentStudent();
        if (Boolean.FALSE.equals(summary.getPublico())
                && !summary.getStudent().getId().equals(student.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Este resumo é privado"
            );
        }
        if (summary.getAtivo().equals(Boolean.FALSE)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Este resumo esta desativado"
            );
        }

        return toResponse(summary, true);
    }

    @Transactional(readOnly = true)
    public List<SummaryGetResponseDTO> getStudentSummary(Long id, int limit){
        Student student = studentService.resolveStudentByIdOrUserId(id);

    int pageSize = Math.max(1, limit);

    return summaryRepository.findByStudentIdAndAtivoTrue(student.getId(), PageRequest.of(0, pageSize))
        .getContent()
            .stream()
                .map(summary -> toResponse(summary, true))
            .filter(item -> Boolean.TRUE.equals(item.ativo()))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<SummaryGetResponseDTO> getStudentSummaryByStudentId(Long studentId, int limit) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Estudante não encontrado"
                ));

        int pageSize = Math.max(1, limit);

        return summaryRepository.findByStudentIdAndAtivoTrue(student.getId(), PageRequest.of(0, pageSize))
                .getContent()
                .stream()
                .map(summary -> toResponse(summary, true))
                .filter((item) -> Boolean.TRUE.equals(item.publico()))
                .toList();
    }

    public SummaryGetResponseDTO update(Long id, SummaryUpdateRequestDTO dto) {
        Summary existing = summaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resumo não encontrado"));

        Subject subject = subjectRepository.findById(dto.materiaId())
                        .orElseThrow(() -> new RuntimeException("Erro ao encontrar a materia"));

        existing.getTagLinks().clear();
        summaryRepository.saveAndFlush(existing);
        if (dto.tags() != null && !dto.tags().isEmpty()) {

            var tagsById = tagsRepository.findAllById(dto.tags())
                    .stream()
                    .collect(java.util.stream.Collectors.toMap(Tags::getId, tag -> tag));

            for (Long tagId : dto.tags()) {
                Tags tag = tagsById.get(tagId);
                if (tag == null) {
                    throw new RuntimeException("Tag não encontrada: " + tagId);
                }
                if (!Boolean.TRUE.equals(tag.getAtivo())) {
                    throw new RuntimeException("Tag inativa: " + tagId);
                }

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

    public List<SummaryGetResponseDTO> getBySubjectId(Long subjectId, int limit) {
        int pageSize = Math.max(1, limit);

        return summaryRepository.findBySubjectIdAndAtivoTrue(subjectId, PageRequest.of(0, pageSize))
                .getContent()
                .stream()
                .map(summary -> toResponse(summary, true))
                .toList();
    }

    public void reportar(Long id, Long studentId){
        Summary summary = summaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o resumo!"));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        if (reportRepository.existsByStudentAndSummary(student, summary)) {
            throw new RuntimeException("Você já reportou este resumo");
        }

        Report report = new Report();
        report.setStudent(student);
        report.setSummary(summary);
        report.setReportado(true);

        reportRepository.save(report);

    }

    public void atualizar_status(Long id){
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

        if (!summary.getStudent().getId().equals(studentId)) {
            throw new RuntimeException("Você não tem permissão para alterar este resumo");
        }

        summary.setPublico(!summary.getPublico());

        summaryRepository.save(summary);
    }
}