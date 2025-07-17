package com.englishscenario.backend.controller;

import com.englishscenario.backend.dto.VocabularyRequest;
import com.englishscenario.backend.dto.VocabularyResponse;
import com.englishscenario.backend.service.VocabularyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 词汇生成控制器
 * 处理词汇生成相关的HTTP请求
 */
@RestController
@RequestMapping("/api/vocabulary")
@RequiredArgsConstructor  // 自动生成构造函数注入
@Slf4j
public class VocabularyController {

    private final VocabularyService vocabularyService;

    /**
     * 生成词汇接口
     * POST /api/vocabulary/generate
     *
     * @param request 包含场景的请求体
     * @return 生成的词汇响应
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateVocabulary(
            @RequestBody VocabularyRequest request,
            @RequestParam(required = false) String username) {

        log.info("Received vocabulary generation request: {} from user: {}", request, username);

        // 验证请求
        if (!request.isValid()) {
            log.warn("Invalid request: scenario is empty");
            return ResponseEntity
                    .badRequest()
                    .body("场景描述不能为空");
        }

        try {
            // 调用服务生成词汇，传递语言参数和用户名
            VocabularyResponse response = vocabularyService.generateVocabulary(
                    request.getScenario(),
                    request.getLanguage(),
                    username
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error generating vocabulary", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("生成词汇时发生错误，请稍后重试");
        }
    }

    /**
     * 测试接口
     * GET /api/vocabulary/test
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Vocabulary API is working!");
    }
}