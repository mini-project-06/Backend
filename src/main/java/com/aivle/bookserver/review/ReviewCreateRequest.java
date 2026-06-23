package com.aivle.bookserver.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// 리뷰 등록 시 사용하는 DTO
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateRequest {

    @NotNull(message = "도서 ID(bookId)는 필수 항목입니다.")
    private Long bookId;

    @NotBlank(message = "닉네임은 공백이거나 비어있을 수 없습니다.")
    private String nickname;
    
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @Min(value = 1, message = "평점은 최소 1점 이상이어야 합니다.")
    @Max(value = 5, message = "평점은 최대 5점 이하이어야 합니다.")
    private int rating;

    private String content;
}