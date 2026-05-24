package com.techub.api.service;

import com.techub.api.domain.*;
import com.techub.api.dto.*;
import com.techub.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SummaryService {

    @Autowired
    private SummaryRepository summaryRepository;
    @Autowired
    private LikesService likesService;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private final CourseRepository courseRepository;
    @Autowired
    private final StudentRepository studentRepository;
    @Autowired
    private final StudentService studentService;

    public SummaryService(SummaryRepository summaryRepository,
                          CourseRepository courseRepository,
                          StudentRepository studentRepository,
                          StudentService studentService) {
        this.summaryRepository = summaryRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.studentService = studentService;
    }

    public SummaryCreateResponseDTO saveSummary(SummaryCreateRequestDTO dto, Long id) {

        if (dto.titulo() == null || dto.titulo().isEmpty()) {
            throw new IllegalArgumentException("Título é obrigatório");
        }

        if (dto.conteudo() == null || dto.conteudo().isEmpty()) {
            throw new IllegalArgumentException("Conteudo é obrigatório");
        }

        Student student = studentService.resolveStudentByIdOrUserId(id);

        Summary summary = new Summary();
        summary.setTitulo(dto.titulo());
        summary.setConteudo(dto.conteudo());
        summary.setStudent(student);

        Course course = courseRepository.findTopByOrderByIdAsc()
            .orElseThrow(() -> new RuntimeException("Curso DSM não encontrado"));
        summary.setCourse(course);

        try {
            summaryRepository.save(summary);
            return new SummaryCreateResponseDTO(student.getId(), summary.getTitulo(), summary.getConteudo());
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

        return new SummaryGetResponseDTO(
                summary.getStudent().getId(),
                summary.getId(),
                summary.getTitulo(),
                summary.getConteudo(),
                totalReports,
                summary.getPublico(),
                summary.getAtivo(),
                totalCurtidas
        );
    }

    private SummaryListResponseDTO toListResponse(Summary summary, boolean includeLikes) {
        Long totalCurtidas = null;

        Integer totalReports = Math.toIntExact(reportRepository.countBySummaryAndReportadoTrue(summary));

        if (includeLikes && Boolean.TRUE.equals(summary.getAtivo()) && Boolean.TRUE.equals(summary.getPublico())) {
            totalCurtidas = likesService.contarCurtidas(summary);
        }

        String studentUrl = summary.getStudent().getAvatar() != null ? summary.getStudent().getAvatar().getUrl() : null;

        return new SummaryListResponseDTO(
                summary.getStudent().getId(),
                summary.getStudent().getNome(),
                studentUrl,
                summary.getId(),
                summary.getTitulo(),
                summary.getConteudo(),
                totalReports,
                summary.getPublico(),
                summary.getAtivo(),
                totalCurtidas
        );
    }

    public List<SummaryListResponseDTO> getAll(int limit) {
        int pageSize = Math.max(1, limit);

        return summaryRepository.findActive(PageRequest.of(0, pageSize))
                .getContent()
                .stream()
                .map(summary -> toListResponse(summary, true))
                .toList();
    }

    public List<SummaryGetResponseDTO> findByAtivoTrue(int limit) {
        int pageSize = Math.max(1, limit);

        return summaryRepository.findActive(PageRequest.of(0, pageSize))
                .stream()
                .filter(summary -> summary.getPublico() == true)
            .map(summary -> toResponse(summary, true))
                .toList();
    }

    public List<SummaryGetResponseDTO> findByAtivoFalse(int limit) {
        int pageSize = Math.max(1, limit);

        return summaryRepository.findInactive(PageRequest.of(0, pageSize))
                .stream()
            .map(summary -> toResponse(summary, false))
                .toList();
    }

    public SummaryGetResponseDTO getById(Long id) {
        Summary summary = summaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resumo não encontrado"));
        return toResponse(summary, true);
    }

        public List<SummaryListResponseDTO> getStudentSummary(Long id, int limit){
            Student student = studentService.resolveStudentByIdOrUserId(id);

        int pageSize = Math.max(1, limit);

        return summaryRepository.findByStudentIdAndAtivoTrue(student.getId(), PageRequest.of(0, pageSize))
            .getContent()
                .stream()
                    .map(summary -> toListResponse(summary, true))
                .toList();
    }

    public SummaryGetResponseDTO update(Long id, SummaryUpdateRequestDTO dto) {
        Summary existing = summaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resumo não encontrado"));

        existing.setTitulo(dto.titulo());
        existing.setConteudo(dto.conteudo());

        summaryRepository.save(existing);
        return toResponse(existing, true);
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