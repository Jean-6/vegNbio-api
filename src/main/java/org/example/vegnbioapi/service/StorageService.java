package org.example.vegnbioapi.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface StorageService {
    List<String> uploadPdf(String userId,List<MultipartFile> files);
    void deleteFromS3(List<String> pictureUrls);
    String extractKeyFromUrl(String url);
    List<String> uploadPictures(String directory,List<MultipartFile> pictures) throws IOException;
}
