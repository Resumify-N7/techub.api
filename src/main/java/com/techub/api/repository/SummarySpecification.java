package com.techub.api.repository;

import com.techub.api.domain.Summary;
import org.springframework.data.jpa.domain.Specification;

public class SummarySpecification {

    public static Specification<Summary> byCourseId(Long courseId) {
        return (root, query, cb) ->
                courseId == null ? null : cb.equal(root.get("course").get("id"), courseId);
    }

    public static Specification<Summary> byUniversityId(Long universityId) {
        return (root, query, cb) ->
                universityId == null ? null : cb.equal(
                        root.get("course").get("university").get("id"), universityId);
    }

    public static Specification<Summary> byTagId(Long tagId) {
        return (root, query, cb) -> {
            if (tagId == null) return null;
            var tags = root.join("tags");
            return cb.equal(tags.get("id"), tagId);
        };
    }

    public static Specification<Summary> bySemestre(Integer semestre) {
        return (root, query, cb) ->
                semestre == null ? null : cb.equal(
                        root.get("subject").get("semestre"), semestre);
    }
}