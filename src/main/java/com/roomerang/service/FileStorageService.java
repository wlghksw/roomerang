package com.roomerang.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final String uploadDir = "uploads/"; // 업로드 폴더 경로

    public String storeFile(MultipartFile file) {
        try {
            //디렉토리가 없으면 생성
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            //파일 이름 생성 (UUID 사용)
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + filename);

            //파일 저장
            Files.write(filePath, file.getBytes());

            return "/uploads/" + filename; //저장된 이미지의 URL 반환
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }
}

