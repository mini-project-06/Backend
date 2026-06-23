package com.aivle.bookserver.book;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Pattern;

public record BookUpdateRequest(
        String title,
        String author,
        String content,

        @Pattern(
                regexp = "000 총류|100 철학|200 종교|300 사회과학|400 자연과학|500 기술과학|600 예술|700 언어|800 문학",
                message = "올바른 카테고리를 입력해주세요."
                )
        String category,
        String coverImageUrl,

        @JsonProperty("avg_rating")
        Double avgRating,

        @JsonProperty("rate_point")
        Double ratePoint,

        Integer reviewCount
) {
    public Book toEntity() {
        return Book.builder()
                .title(this.title)
                .author(this.author)
                .content(this.content)
                .category(this.category)
                .coverImageUrl(this.coverImageUrl)
                .build();
    }

}
