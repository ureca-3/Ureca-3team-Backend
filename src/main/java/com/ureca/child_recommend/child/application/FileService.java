package com.ureca.child_recommend.child.application;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;


    public String storeFile(MultipartFile file) throws IOException {
        // 저장 디렉토리 경로가 없으면 생성
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 파일 이름 생성 (UUID 사용)
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + "_" + originalFilename;
        Path filePath = uploadPath.resolve(fileName);

        // 파일 저장
        file.transferTo(filePath.toFile());

        // 저장된 파일의 URL 반환 (URL 형식으로 만들어줌)
        return "/profile/" + fileName;
    }
}
