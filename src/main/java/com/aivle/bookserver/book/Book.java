package com.aivle.bookserver.book;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String author;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String category;

    @Column(columnDefinition = "TEXT")
    private String coverImageUrl;

    // TODO: [임시 구현 - 캐싱 로직] AI가 생성한 한 줄 소개글 캐싱용 컬럼 (팀원 검토 후 수정 가능)
    @Column(columnDefinition = "TEXT")
    private String aiCopy;


    @JsonProperty("avg_rating")
    @Builder.Default
    private Double avgRating = 0.0;

    @JsonProperty("rate_point")
    @Builder.Default
    private Double ratePoint = 0.0;

    @Builder.Default
    private Integer reviewCount = 0;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
