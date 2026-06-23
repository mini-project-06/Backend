package com.aivle.bookserver.review;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// 리뷰 삭제 시 사용하는 DTO (비밀번호 확인용)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDeleteRequest {
    @NotBlank
    private String password;
}