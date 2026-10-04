package com.backend.crosswords.corpus.services;

import com.backend.crosswords.admin.models.User;
import com.backend.crosswords.corpus.dto.CreateUpdateAnnotationDTO;
import com.backend.crosswords.corpus.models.Annotation;
import com.backend.crosswords.corpus.models.DocMeta;
import com.backend.crosswords.corpus.repositories.jpa.AnnotationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class AnnotationService {
    private final AnnotationRepository annotationRepository;
    private final ModelMapper modelMapper;

    public AnnotationService(AnnotationRepository annotationRepository, ModelMapper modelMapper) {
        this.annotationRepository = annotationRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public Annotation createAnnotation(User user, DocMeta docMeta, CreateUpdateAnnotationDTO createUpdateAnnotationDTO) {
        Annotation annotation = modelMapper.map(createUpdateAnnotationDTO, Annotation.class);
        annotation.setDoc(docMeta);
        annotation.setOwner(user);
        return annotationRepository.save(annotation);
    }

    @Transactional
    public void deleteAnnotationByIdFromDoc(User user, DocMeta docMeta, Long annotationId) {
        var annotation = annotationRepository.findById(annotationId).orElseThrow(() -> new NoSuchElementException("There is no annotations with such id"));
        if (!Objects.equals(user.getId(), annotation.getOwner().getId())) {
            throw new IllegalArgumentException("You are not the owner of this annotation");
        }
        if (!Objects.equals(docMeta.getId(), annotation.getDoc().getId())) {
            throw new IllegalArgumentException("This documents doesn't own this annotation");
        }
        annotation.setDoc(null);
        annotation.setOwner(null);
        docMeta.getAnnotations().remove(annotation);
        annotationRepository.delete(annotation);
    }

    @Transactional
    public void updateAnnotationByIdForDoc(User user, DocMeta docMeta, Long annotationId, CreateUpdateAnnotationDTO createUpdateAnnotationDTO) {
        var annotation = annotationRepository.findById(annotationId).orElseThrow(() -> new NoSuchElementException("There is no comments with such id"));
        if (!Objects.equals(user.getId(), annotation.getOwner().getId())) {
            throw new IllegalArgumentException("You are not the owner of this comment");
        }
        if (!Objects.equals(docMeta.getId(), annotation.getDoc().getId())) {
            throw new IllegalArgumentException("This documents doesn't own this comment");
        }
        annotation.setComments(createUpdateAnnotationDTO.getComments());
        annotation.setStartPos(createUpdateAnnotationDTO.getStartPos());
        annotation.setEndPos(createUpdateAnnotationDTO.getEndPos());
        annotationRepository.save(annotation);
    }
}
