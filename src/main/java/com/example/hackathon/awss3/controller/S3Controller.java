package com.example.hackathon.awss3.controller;

import com.example.hackathon.awss3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    // S3에 이미지 업로드 (POST 요청)
    @PostMapping("/s3/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        // JSON 형식으로 응답
        Map<String, String> response = new HashMap<>();

        try {
            String fileUrl = s3Service.uploadImage(file); // 이미지 업로드 후 URL 반환
            response.put("imageUrl", fileUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage() != null ? e.getMessage() : "파일 업로드 중 오류 발생");
            return ResponseEntity.status(500).body(response); // 500 Internal Server Error
        }
    }

    // S3에서 이미지 삭제 (DELETE 요청, URL 전체 경로 입력)
    @DeleteMapping("/s3/delete")
    public ResponseEntity<Map<String, String>> deleteImage(@RequestParam("fileUrl") String fileUrl) {
        // JSON 형식으로 응답
        Map<String, String> response = new HashMap<>();

        try {
            s3Service.deleteImage(fileUrl); // URL을 이용하여 S3에서 삭제
            response.put("message", "파일 삭제 완료");
            response.put("deletedFileUrl", fileUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage() != null ? e.getMessage() : "파일 삭제 중 오류 발생");
            return ResponseEntity.status(500).body(response); // 500 Internal Server Error
        }
    }
}
