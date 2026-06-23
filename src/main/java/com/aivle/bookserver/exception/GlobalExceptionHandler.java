package com.aivle.bookserver.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(BookNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleBookNotFound(BookNotFoundException e) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(
                                404,
                                e.getMessage(),
                                LocalDateTime.now()
                        ));
        }

        @ExceptionHandler(ReviewNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleReviewNotFound(ReviewNotFoundException e) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(
                                404,
                                e.getMessage(),
                                LocalDateTime.now()
                        ));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
                String message = e.getBindingResult().getFieldErrors()
                        .stream()
                        .map(f -> f.getDefaultMessage())
                        .findFirst()
                        .orElse("입력값이 올바르지 않습니다.");
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse(
                                400,
                                message,
                                LocalDateTime.now()
                        ));
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse(
                                400,
                                e.getMessage(),
                                LocalDateTime.now()
                        ));
        }


        // 필수 RequestParam 누락
        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<ErrorResponse> handleMissingParam(
                MissingServletRequestParameterException e) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        400,
                        "필수 요청 파라미터가 누락되었습니다: " + e.getParameterName(),
                        LocalDateTime.now()
                ));
        }


        // PathVariable 타입 오류 (예: /books/abc)
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ErrorResponse> handleTypeMismatch(
                MethodArgumentTypeMismatchException e) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        400,
                        "요청 값의 타입이 올바르지 않습니다: " + e.getName(),
                        LocalDateTime.now()
                ));
        }


        // 깨진 JSON 요청 처리
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handleNotReadable(
                HttpMessageNotReadableException e) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        400,
                        "요청 본문(JSON) 형식이 올바르지 않습니다.",
                        LocalDateTime.now()
                ));
        }


        // 지원하지 않는 HTTP Method
        @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
        public ResponseEntity<ErrorResponse> handleMethodNotSupported(
                HttpRequestMethodNotSupportedException e) {

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErrorResponse(
                        405,
                        "지원하지 않는 HTTP 메서드입니다: " + e.getMethod(),
                        LocalDateTime.now()
                ));
        }


        // Fallback Handler (예상하지 못한 예외 처리)
         @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleException(Exception e) {

                // 서버 로그 출력
                log.error("처리되지 않은 예외 발생", e);

                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorResponse(
                                500,
                                "서버 오류가 발생했습니다.",
                                LocalDateTime.now()
                        ));
        }

}