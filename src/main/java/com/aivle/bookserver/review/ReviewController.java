package com.aivle.bookserver.review;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public List<Review> getReviews(@RequestParam Long bookId) {
        return reviewService.getReviews(bookId);
    }

    @PostMapping
    public ResponseEntity<Review> createReview(@Valid @RequestBody ReviewCreateRequest request) {
        // 클라이언트로부터 ReviewCreateRequest DTO를 받아 서비스로 전달합니다.
        Review review = reviewService.createReview(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }


    /**
     * 현재: FE가 클라이언트에서 password 검증 후 호출 → 서버는 단순 삭제
     * BE2 이관 시: 요청에 password 추가하여 서버 검증으로 전환
     */
    @DeleteMapping("/{id}") 
    public ResponseEntity<Void> deleteReview(@PathVariable Long id, @Valid @RequestBody ReviewDeleteRequest request) {
        reviewService.deleteReview(id, request); // deleteReview() 예외 처리
        return ResponseEntity.noContent().build();
    }
}
