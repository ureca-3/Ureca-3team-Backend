package com.ureca.child_recommend.child.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir; // 이미지 저장 경로

    public String storeFile(MultipartFile file) throws IOException {
        // 파일 이름 생성 (중복 방지)
        String originalFilename = file.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID() + "_" + originalFilename;

        // 파일 저장 경로
        Path filePath = Paths.get(uploadDir + File.separator + uniqueFileName);
        Files.createDirectories(filePath.getParent()); // 디렉토리 생성
        Files.write(filePath, file.getBytes()); // 파일 저장

        // 파일 URL 생성 (상대 경로 또는 절대 경로에 맞게 수정)
        return "/uploads/" + uniqueFileName; // URL 반환
    }
}

