package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.service.S3FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/file")
public class S3FileController {

    @Autowired
    public S3FileService s3FileService;

    @PostMapping("/upload")
    private String uploadFile(MultipartFile file){
       return s3FileService.uploadFile(file);
    }

    @GetMapping("/get")
    private ResponseEntity<ByteArrayResource> getFile(@RequestParam String filename) throws IOException {
        byte[] file = s3FileService.getFile(filename);
        ByteArrayResource resource =new ByteArrayResource(file);
        return ResponseEntity
                .ok()
                .contentLength(file.length)
                .header("Content-type","application/octet-stream")
                .header("Content-disposition","attachment; filename=\""+filename+"\"")
                .body(resource);
    }

    @GetMapping("/delete")
    private String deleteFile(@RequestParam String filename){
       return s3FileService.deleteFile(filename);
    }


}
