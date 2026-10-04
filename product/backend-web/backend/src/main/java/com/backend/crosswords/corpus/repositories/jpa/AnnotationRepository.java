package com.backend.crosswords.corpus.repositories.jpa;

import com.backend.crosswords.corpus.models.Annotation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnotationRepository extends JpaRepository<Annotation, Long> {
}
