package com.backend.crosswords.corpus.services;

import com.backend.crosswords.admin.models.User;
import com.backend.crosswords.admin.services.UserService;
import com.backend.crosswords.corpus.models.DocMeta;
import com.backend.crosswords.corpus.models.DocRating;
import com.backend.crosswords.corpus.models.DocRatingId;
import com.backend.crosswords.corpus.repositories.jpa.DocRatingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DocRatingService {
    private final DocRatingRepository ratingRepository;
    private final UserService userService;

    public DocRatingService(DocRatingRepository ratingRepository, UserService userService) {
        this.ratingRepository = ratingRepository;
        this.userService = userService;
    }

    public Optional<DocRating> findById(DocRatingId ratingId) {
        return ratingRepository.findById(ratingId);
    }

    public void createRating(DocMeta doc, User user, Integer summaryRating, Integer classificationRating) {
        DocRatingId ratingId = new DocRatingId(doc.getId(), user.getId());
        DocRating rating;
        var check = ratingRepository.findById(ratingId);
        if (check.isPresent()) {
            rating = check.get();
        } else {
            rating = new DocRating();
            rating.setId(ratingId);
            rating.setDoc(doc);
            rating.setUser(user);
        }
        rating.setSummaryRating(summaryRating);
        rating.setClassificationRating(classificationRating);
        ratingRepository.save(rating);
    }
    @Transactional
    public void dropRatings(List<DocRating> ratings, Boolean dropRatingSummary, Boolean dropRatingClassification) {
        var iterator = ratings.iterator();
        while (iterator.hasNext()) {
            DocRating rating = iterator.next();
            if (dropRatingSummary && dropRatingClassification) {
                iterator.remove();
                userService.removeRating(rating);
                ratingRepository.delete(rating);
            } else {
                if (dropRatingSummary) {
                    rating.setSummaryRating(null);
                }
                if (dropRatingClassification) {
                    rating.setClassificationRating(null);
                }
                ratingRepository.save(rating);
            }
        }
    }

    public List<Integer> getRatingsForDocumentByUser(DocMeta docMeta, User user) {
        var rating = ratingRepository.findByUserAndDoc(user, docMeta);
        List<Integer> result = new ArrayList<>();
        if (rating.isEmpty()) {
            result.add(null);
            result.add(null);
            return result;
        }
        result.add(rating.get().getSummaryRating());
        result.add(rating.get().getClassificationRating());
        return result;
    }
}
