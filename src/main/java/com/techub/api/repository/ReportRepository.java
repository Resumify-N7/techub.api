package com.techub.api.repository;

import com.techub.api.domain.Report;
import com.techub.api.domain.Student;
import com.techub.api.domain.Summary;

public interface ReportRepository extends SoftDeleteRepository<Report, Long> {

    boolean existsByStudentAndSummary(Student student, Summary summary);

    long countBySummaryAndReportadoTrue(Summary summary);

    void deleteBySummary(Summary summary);
}