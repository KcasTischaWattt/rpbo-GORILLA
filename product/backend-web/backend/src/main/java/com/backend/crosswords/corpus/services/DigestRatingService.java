package com.backend.crosswords.corpus.services;

import com.backend.crosswords.admin.models.User;
import com.backend.crosswords.admin.services.UserService;
import com.backend.crosswords.corpus.models.DigestCore;
import com.backend.crosswords.corpus.models.DigestCoreRating;
import com.backend.crosswords.corpus.models.DigestCoreRatingId;
import com.backend.crosswords.corpus.repositories.jpa.DigestCoreRatingRepository;
import org.springframework.stereotype.Service;

@Service
public class DigestRatingService {
    private final DigestCoreRatingRepository ratingRepository;

    public DigestRatingService(DigestCoreRatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public Double getCoresAverageRating(DigestCore core) {
        Double averageRating = 0.0;
        var ratings = ratingRepository.findAllByCore(core);
        var ratingSize = ratings.size();
        for (var rating : ratings) {
            if (rating.getDigestCoreRating() == null) {
                --ratingSize;
            } else {
                averageRating += rating.getDigestCoreRating();
            }
        }
        if (ratingSize == 0) {
            return null;
        }
        return averageRating / ratingSize;
    }

    public Integer getCoresUsersRating(DigestCore core, User user) {
        var rating = ratingRepository.findByCoreAndUser(core, user);
        return rating.isEmpty() ? null : rating.get().getDigestCoreRating();
    }

    public void createRating(DigestCore core, User user, Integer digestCoreRating) {
        DigestCoreRatingId ratingId = new DigestCoreRatingId(core.getId(), user.getId());
        DigestCoreRating rating;
        var check = ratingRepository.findById(ratingId);
        if (check.isPresent()) {
            rating = check.get();
        } else {
            rating = new DigestCoreRating();
            rating.setId(ratingId);
            rating.setCore(core);
            rating.setUser(user);
        }
        rating.setDigestCoreRating(digestCoreRating);
        ratingRepository.save(rating);
    }
}
