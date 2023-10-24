package org.example.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@Service
public class S3FileService {

    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    public String uploadFile(MultipartFile newFile) {
        String responseString;
        File fileObj = convertFile(newFile);
        String filename = newFile.getOriginalFilename();
        try {
            S3Object object = s3Client.getObject(bucketName, filename);
            responseString = filename + " is Already Present";
            log.info(responseString);
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == 404) {
                s3Client.putObject(bucketName, filename, fileObj);
                fileObj.delete();
                responseString =filename + " File Upload SuccessFully" ;
                log.info(responseString);
            } else {
                throw e;
            }
        }
        return responseString;
    }

    public byte[] getFile(String filename) throws IOException {
        S3Object object = s3Client.getObject(bucketName, filename);
        return IOUtils.toByteArray(object.getObjectContent());
    }

    public String deleteFile(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
        return fileName + " removed ...";
    }

    private File convertFile(MultipartFile newFile) {

        File convertedFile = new File(Objects.requireNonNull(newFile.getOriginalFilename()));

        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(newFile.getBytes());
        } catch (Exception e) {
            log.info("Failed To Convert File : message -> {}" + e.getMessage());
        }
        return convertedFile;
    }

}
