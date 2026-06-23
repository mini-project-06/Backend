package com.aivle.bookserver.book;
import com.aivle.bookserver.rating.RatingService;
import com.aivle.bookserver.review.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.LocalDateTime;
import com.aivle.bookserver.exception.BookNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final RatingService ratingService;

    @Transactional(readOnly = true)
    public List<Book> getBooks(String category) {
        if (category != null && !category.isBlank()) {
            return bookRepository.findByCategory(category);
        }
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Book getBook(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id)); // 예외 처리
    }

    @Transactional
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(Long id, BookUpdateRequest req) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));

        //수정 시 공백 타이틀 방지용인데 지우셔도 됨
        if (req.title() != null) {
            if (req.title().isBlank()) {
                throw new IllegalArgumentException("수정할 제목은 공백일 수 없습니다.");
            }
            book.setTitle(req.title());
        }
        if (req.author()        != null) book.setAuthor(req.author());
        if (req.content()       != null) book.setContent(req.content());
        if (req.category()      != null) book.setCategory(req.category());
        if (req.coverImageUrl() != null) book.setCoverImageUrl(req.coverImageUrl());
        
        return bookRepository.save(book);
    }
    //표지 url 업데이트 메서드 (미션7)
    @Transactional
    public Book updateBookCover(Long id, String coverImageUrl) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        book.setCoverImageUrl(coverImageUrl);
        book.setUpdatedAt(LocalDateTime.now());
        return bookRepository.save(book);
    }

    @Transactional
    public void deleteBook(Long id) {
        if (bookRepository.existsById(id)) {
            reviewRepository.deleteAllByBookId(id);
            bookRepository.deleteById(id);
            // 삭제 후 나머지 책들의 글로벌 평균 재계산
            bookRepository.findByAvgRatingGreaterThan(0.0)
                    .forEach(b -> ratingService.recalculate(b.getId()));
        } else {
            throw new BookNotFoundException(id);
        }
    }

    @Transactional(readOnly = true)
    public List<Book> getRelatedTop3(Long bookId, String category) {
        return bookRepository.findTop3ByCategoryAndIdNotOrderByRatePointDesc(category, bookId);
    }

    // TODO: [임시 구현 - 캐싱 로직] AI 소개 문구를 DB에 캐싱하는 전용 메서드 (팀원 검토 후 수정 가능)
    @Transactional
    public Book updateBookAiCopy(Long id, String aiCopy) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        book.setAiCopy(aiCopy);
        book.setUpdatedAt(LocalDateTime.now());
        return bookRepository.save(book);
    }
}

