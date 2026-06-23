package com.aivle.bookserver.rating;

import com.aivle.bookserver.book.Book;
import com.aivle.bookserver.book.BookRepository;
import com.aivle.bookserver.review.Review;
import com.aivle.bookserver.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aivle.bookserver.exception.BookNotFoundException;

import java.util.List;

/**
 * 평점 재계산 서비스 (프론트엔드 로직과 동일)
 *
 * avg_rating  = round(리뷰 평균, 1)
 * rate_point  = 베이지안 가중 평점
 *   C = 5, m = avg_rating > 0 인 전체 도서 평균 (없으면 3.5)
 *   rate_point = round((C*m + ratingSum) / (C + reviewCount), 1)
 */
@Service
@RequiredArgsConstructor
public class RatingService {

    private static final double C = 5.0;
    private static final double DEFAULT_M = 3.5;

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public void recalculate(Long bookId) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new BookNotFoundException(bookId));  // 예외 처리

        List<Review> reviews = reviewRepository.findByBookId(bookId);

        if (reviews.isEmpty()) {
            book.setAvgRating(0.0);
            book.setRatePoint(0.0);
            book.setReviewCount(0);
            bookRepository.save(book);
            return;
        }

        double ratingSum = reviews.stream().mapToInt(Review::getRating).sum();
        double avgRating = Math.round((ratingSum / reviews.size()) * 10.0) / 10.0;

        List<Book> booksWithRatings = bookRepository.findByAvgRatingGreaterThan(0.0);
        double m = booksWithRatings.isEmpty()
                ? DEFAULT_M
                : booksWithRatings.stream().mapToDouble(Book::getAvgRating).average().orElse(DEFAULT_M);

        double ratePoint = Math.round(((C * m + ratingSum) / (C + reviews.size())) * 10.0) / 10.0;

        book.setAvgRating(avgRating);
        book.setRatePoint(ratePoint);
        book.setReviewCount(reviews.size());
        bookRepository.save(book);
    }
}
