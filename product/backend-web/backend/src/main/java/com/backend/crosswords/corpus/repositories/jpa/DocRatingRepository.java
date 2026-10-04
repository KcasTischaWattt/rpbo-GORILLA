package com.backend.crosswords.corpus.repositories.jpa;

import com.backend.crosswords.admin.models.User;
import com.backend.crosswords.corpus.models.DocMeta;
import com.backend.crosswords.corpus.models.DocRating;
import com.backend.crosswords.corpus.models.DocRatingId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocRatingRepository extends JpaRepository<DocRating, DocRatingId> {
    Optional<DocRating> findByUserAndDoc(User user, DocMeta docMeta);
}
