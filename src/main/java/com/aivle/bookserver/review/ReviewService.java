package com.aivle.bookserver.review;

import com.aivle.bookserver.rating.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aivle.bookserver.exception.ReviewNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RatingService ratingService;

    @Transactional(readOnly = true)
    public List<Review> getReviews(Long bookId) {
        return reviewRepository.findByBookId(bookId);
    }

    @Transactional
    public Review createReview(ReviewCreateRequest request) {
        Review review = Review.builder()
                .bookId(request.getBookId())
                .nickname(request.getNickname())
                .password(request.getPassword()) 
                .rating(request.getRating())
                .content(request.getContent())
                .build();
        Review saved = reviewRepository.save(review);

        ratingService.recalculate(request.getBookId());
        return saved;
    }

    @Transactional
    public void deleteReview(Long reviewId, ReviewDeleteRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
        if (!review.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        
        Long bookId = review.getBookId();
        reviewRepository.deleteById(reviewId);
        ratingService.recalculate(bookId);
    }
}
