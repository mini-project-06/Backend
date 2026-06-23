package com.aivle.bookserver.book;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByCategory(String category);

    List<Book> findByAvgRatingGreaterThan(Double avgRating);

    List<Book> findTop3ByCategoryAndIdNotOrderByRatePointDesc(String category, Long id);
}
