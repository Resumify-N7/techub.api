package com.techub.api.repository;

import com.techub.api.domain.Report;
import com.techub.api.domain.Student;
import com.techub.api.domain.Summary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByStudentAndSummary(Student student, Summary summary);

    long countBySummaryAndReportadoTrue(Summary summary);

    void deleteBySummary(Summary summary);
}