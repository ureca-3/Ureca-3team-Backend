package com.ureca.child_recommend.global.application;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.ureca.child_recommend.global.exception.BusinessException;
import com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.io.IOException;

@Slf4j @RequiredArgsConstructor @Component @Service
public class S3Service {
    private final AmazonS3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName ;
    public String uploadFileImage(MultipartFile image, String contentsType) {

        try {
            String fileName = createFileImageName(image.getOriginalFilename(), contentsType);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(image.getContentType());
            metadata.setContentLength(image.getSize());

            s3Client.putObject(bucketName, fileName, image.getInputStream(), metadata);
            return s3Client.getUrl(bucketName, fileName).toString();

        } catch (IOException e) {
            throw new BusinessException(CommonErrorCode.NO_CONTENT_AVAILABLE);
        }
    }
    public String updateFileImage(String imageFileUrl, MultipartFile newImage) {
        try {

            String fileName = imageFileUrl.substring(imageFileUrl.lastIndexOf('/')+1); // 해당 이미지 이름

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(newImage.getContentType());
            metadata.setContentLength(newImage.getSize());

            s3Client.putObject(bucketName, fileName, newImage.getInputStream(), metadata);
            return s3Client.getUrl(bucketName, fileName).toString();

        } catch (IOException e) {
            throw new BusinessException(CommonErrorCode.NO_CONTENT_AVAILABLE);
        }
    }

    private String createFileImageName(String fileName, String contentsType) {

        String fileExtension = "";

        if (fileName != null && fileName.contains(".")) {
            fileExtension = fileName.substring(fileName.lastIndexOf(".")); // 확장자 분리
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return LocalDateTime.now().format(formatter) + contentsType + fileExtension;
    }

    public void deleteFileImage(String imageFileUrl) {

        String fileName = imageFileUrl.substring(imageFileUrl.lastIndexOf('/')+1); // 해당 이미지 이름
        try {
            s3Client.deleteObject(bucketName, fileName);
        } catch (Exception e) {
            throw new BusinessException(CommonErrorCode.NO_CONTENT_AVAILABLE);
        }
    }

}