package com.example.hackathon.awss3.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${AWS_S3_BUCKET_NAME}")
    private String bucketName;

    @Value("${AWS_S3_BASE_PATH}")
    private String basePath; // S3 내부 폴더 경로

    // S3에 이미지 업로드 (basePath/경로에 저장)
    public String uploadImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB 제한
            throw new IllegalArgumentException("파일 크기가 너무 큽니다. (최대 10MB)");
        }

        String fileName = generateFileName(file.getOriginalFilename()); // 업로드할 파일명 생성

        // S3에 파일 업로드
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName) // 버킷 지정
                        .key(fileName) // 저장 경로
                        .acl(ObjectCannedACL.PUBLIC_READ) // 공개 읽기 가능
                        .contentType(file.getContentType()) // 파일 타입 자동 구분
                        .build(),
                RequestBody.fromBytes(file.getBytes())); // 파일을 바이트 배열로 변환하여 업로드

        log.info("파일 업로드 성공: {}", fileName);
        return getFileUrl(fileName); // 업로드된 파일의 URL 반환
    }


    // S3에서 이미지 삭제 (URL 전체 경로 입력)
    public void deleteImage(String fileUrl) {
        String fileKey = extractFileKeyFromUrl(fileUrl); // URL에서 파일 키(경로) 추출

        // S3에서 파일 삭제
        s3Client.deleteObject(DeleteObjectRequest.builder() // 파일 삭제
                .bucket(bucketName) // 버킷 지정
                .key(fileKey) // 파일 키 지정
                .build());

        log.info("파일 삭제 성공: {}", fileKey);
    }

    // S3 이미지 URL 반환
    public String getFileUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, "ap-northeast-2", fileName);
    }

    // 파일명 생성
    private String generateFileName(String originalFileName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")); // 밀리초 단위까지
        String uuid = UUID.randomUUID().toString().replace("-", ""); // 하이픈 제거한 UUID
        String hash = generateSHA256(originalFileName + timestamp + uuid).substring(0, 10); // SHA-256 해시값(10자리)

        return String.format("%s/%s_%s_%s", basePath, timestamp, hash, originalFileName);
    }

    // SHA-256 해시 생성 (파일명 해싱용)
    private String generateSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // 바이트 배열을 16진수 문자열로 변환
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 해시 생성 오류", e);
        }
    }

    // URL에서 S3 파일 키(경로) 추출
    private String extractFileKeyFromUrl(String fileUrl) {
        String urlPrefix = String.format("https://%s.s3.ap-northeast-2.amazonaws.com/", bucketName);
        if (fileUrl.startsWith(urlPrefix)) {
            return fileUrl.substring(urlPrefix.length()); // URL에서 버킷 경로를 제외한 파일 키만 추출
        } else {
            throw new IllegalArgumentException("잘못된 S3 URL 형식입니다.");
        }
    }
}
