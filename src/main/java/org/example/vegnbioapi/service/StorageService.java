package org.example.vegnbioapi.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StorageService {
    List<String> upload(List<MultipartFile> pictures);
    List<String> uploadPdf(String userId,List<MultipartFile> files);
    void deleteFromS3(List<String> pictureUrls);
    String extractKeyFromUrl(String url);
}
