package org.example.vegnbioapi.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface S3Storage {
    List<String> upload(List<MultipartFile> pictures);
}
