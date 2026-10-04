package com.backend.crosswords.corpus.repositories.jpa;

import com.backend.crosswords.admin.models.User;
import com.backend.crosswords.corpus.models.DigestCore;
import com.backend.crosswords.corpus.models.DigestCoreRating;
import com.backend.crosswords.corpus.models.DigestCoreRatingId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DigestCoreRatingRepository extends JpaRepository<DigestCoreRating, DigestCoreRatingId> {
    Optional<DigestCoreRating> findByCoreAndUser(DigestCore core, User user);
    List<DigestCoreRating> findAllByCore(DigestCore core);
}
