package com.backend.crosswords.corpus.services;

import com.backend.crosswords.admin.models.User;
import com.backend.crosswords.corpus.dto.CreateUpdateCommentDTO;
import com.backend.crosswords.corpus.models.Comment;
import com.backend.crosswords.corpus.models.DocMeta;
import com.backend.crosswords.corpus.repositories.jpa.CommentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;

    public CommentService(CommentRepository commentRepository, ModelMapper modelMapper) {
        this.commentRepository = commentRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public Comment commentDoc(User user, DocMeta docMeta, CreateUpdateCommentDTO createUpdateCommentDTO) {
        Comment comment = modelMapper.map(createUpdateCommentDTO, Comment.class);
        comment.setDoc(docMeta);
        comment.setOwner(user);
        comment.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        comment.setUpdatedAt(comment.getCreatedAt());
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteCommentByIdFromDoc(User user, DocMeta docMeta, Long commentId) {
        var comment = commentRepository.findById(commentId).orElseThrow(() -> new NoSuchElementException("There is no comments with such id"));
        if (!Objects.equals(user.getId(), comment.getOwner().getId())) {
            throw new IllegalArgumentException("You are not an owner of this comment");
        }
        if (!Objects.equals(docMeta.getId(), comment.getDoc().getId())) {
            throw new IllegalArgumentException("This documents doesn't own this comment");
        }
        comment.setDoc(null);
        comment.setOwner(null);
        docMeta.getComments().remove(comment);
        commentRepository.delete(comment);
    }

    public List<Comment> getAllUsersCommentsFromDoc(User owner, DocMeta docMeta) {
        return commentRepository.findAllByOwnerAndDoc(owner, docMeta);
    }

    @Transactional
    public Comment updateCommentByIdForDoc(User user, DocMeta docMeta, Long commentId, CreateUpdateCommentDTO createUpdateCommentDTO) {
        var comment = commentRepository.findById(commentId).orElseThrow(() -> new NoSuchElementException("There is no comments with such id"));
        if (!Objects.equals(user.getId(), comment.getOwner().getId())) {
            throw new IllegalArgumentException("You are not an owner of this comment");
        }
        if (!Objects.equals(docMeta.getId(), comment.getDoc().getId())) {
            throw new IllegalArgumentException("This documents doesn't own this comment");
        }
        comment.setText(createUpdateCommentDTO.getText());
        comment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return commentRepository.save(comment);
    }
}
