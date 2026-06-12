package com.techub.api.repository;

import com.techub.api.domain.Summary;
import com.techub.api.domain.TagSummary;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class SummarySpecification {

    public static Specification<Summary> ativo() {
        return (root, query, cb) -> cb.isTrue(root.get("ativo"));
    }

    public static Specification<Summary> publico() {
        return (root, query, cb) -> cb.isTrue(root.get("publico"));
    }

    public static Specification<Summary> comBusca(String busca) {
        if (busca == null || busca.isBlank()) return null;
        return (root, query, cb) -> {
            String pattern = "%" + busca.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("titulo")),            pattern),
                    cb.like(cb.lower(root.get("student").get("nome")), pattern)
            );
        };
    }

    public static Specification<Summary> comSubject(Long subjectId) {
        if (subjectId == null) return null;
        return (root, query, cb) -> cb.equal(root.get("subject").get("id"), subjectId);
    }

    public static Specification<Summary> comSemestre(Integer semestre) {
        if (semestre == null) return null;
        return (root, query, cb) -> cb.equal(root.get("subject").get("semestre"), semestre);
    }

    public static Specification<Summary> comTag(Long tagId) {
        if (tagId == null) return null;
        return (root, query, cb) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<TagSummary> ts = sub.from(TagSummary.class);
            sub.select(ts.get("summary").get("id"))
                    .where(cb.equal(ts.get("tag").get("id"), tagId));
            return root.get("id").in(sub);
        };
    }

    public static Specification<Summary> byCourseId(Long courseId) {
        return (root, query, cb) ->
                courseId == null ? null : cb.equal(root.get("subject").get("course").get("id"), courseId);
    }

    public static Specification<Summary> byUniversityId(Long universityId) {
        return (root, query, cb) ->
                universityId == null ? null : cb.equal(
                        root.get("subject").get("course").get("university").get("id"), universityId);
    }

    public static Specification<Summary> byTagId(Long tagId) {
        return comTag(tagId);
    }

    public static Specification<Summary> bySemestre(Integer semestre) {
        return comSemestre(semestre);
    }
}