package com.aivle.bookserver.book;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${openai.api-key}")
    private String openaiApiKey;

    @GetMapping
    public List<Book> getBooks(@RequestParam(required = false) String category) {
        return bookService.getBooks(category);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBook(id));
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@Valid @RequestBody BookUpdateRequest req) {
        if (req.title() == null || req.title().isBlank()) {
            throw new IllegalArgumentException("제목을 입력해주세요.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(req.toEntity()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id,
                                                @Valid @RequestBody BookUpdateRequest req) {
        return ResponseEntity.ok(bookService.updateBook(id, req));
    }

    @PatchMapping("/{id}/cover")
    public ResponseEntity<Book> updateBookCover(@PathVariable Long id,
                                                @RequestBody Map<String, String> body) {
        String coverImageUrl = body.get("coverImageUrl");
        if (coverImageUrl == null) {
            throw new IllegalArgumentException("coverImageUrl 필드는 필수입니다.");
        }
        return ResponseEntity.ok(bookService.updateBookCover(id, coverImageUrl));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/related")
    public ResponseEntity<List<Book>> getRelated(@PathVariable Long id) {
        Book book = bookService.getBook(id);
        return ResponseEntity.ok(bookService.getRelatedTop3(id, book.getCategory()));
    }

    // TODO: [임시 구현 - 캐싱 로직] AI 소개글 캐싱 처리 (팀원 검토 후 수정 가능)
    private static final tools.jackson.databind.ObjectMapper objectMapper = new tools.jackson.databind.ObjectMapper();

    @PostMapping("/ai-intro")
    public ResponseEntity<String> getAiIntro(@RequestBody Map<String, Object> body) {
        try {
            // 1. 요청 본문에서 책 ID 추출
            Long id = null;
            if (body.get("id") != null) {
                id = Long.valueOf(body.get("id").toString());
            }

            // 2. DB에 이미 캐싱된 AI 소개 문구가 있으면 바로 반환
            if (id != null) {
                Book book = bookService.getBook(id);
                if (book.getAiCopy() != null && !book.getAiCopy().isBlank()) {
                    // OpenAI API 응답 형식과 동일하게 포맷팅하여 반환
                    String cachedResponse = String.format(
                        "{\"choices\":[{\"message\":{\"content\":\"%s\"}}]}",
                        book.getAiCopy().replace("\"", "\\\"").replace("\n", "\\n")
                    );
                    return ResponseEntity.ok(cachedResponse);
                }
            }

            // 3. 캐싱된 문구가 없으면 OpenAI API 호출
            String title = (String) body.get("title");
            String author = (String) body.get("author");

            String prompt = String.format(
                "{\"model\":\"gpt-4o-mini\",\"messages\":[{\"role\":\"user\",\"content\":\"\\\"%s\\\" (%s) 책을 한 문장으로 매력적으로 소개해줘. 30자 이내로.\"}],\"max_tokens\":100}",
                title, author
            );

            var request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + openaiApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(prompt))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            // 4. API 호출이 성공적이고 책 ID가 있다면 결과 파싱하여 DB에 캐시 저장
            if (response.statusCode() == 200 && id != null) {
                try {
                    tools.jackson.databind.JsonNode rootNode = objectMapper.readTree(responseBody);
                    String generatedIntro = rootNode.path("choices").get(0).path("message").path("content").asText();
                    if (generatedIntro != null && !generatedIntro.isBlank()) {
                        bookService.updateBookAiCopy(id, generatedIntro);
                    }
                } catch (Exception parseEx) {
                    log.error("OpenAI 응답 파싱 및 DB 저장 실패", parseEx);
                }
            }

            return ResponseEntity.status(response.statusCode()).body(responseBody);
        } catch (Exception e) {
            log.error("AI 소개 생성 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": {\"message\": \"AI 소개 생성 중 오류가 발생했습니다.\"}}");
        }
    }


    //표지 이미지 생성 기능
    /*
    @PostMapping("/image/generate")
    public ResponseEntity<String> proxyImageGeneration(@RequestBody String body,
                                                        @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            var requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/images/generations"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body));

            if (authHeader != null) {
                requestBuilder.header("Authorization", authHeader);
            }

            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.status(response.statusCode()).body(response.body());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": {\"message\": \"" + e.getMessage() + "\"}}");
        }
    }
    */
}