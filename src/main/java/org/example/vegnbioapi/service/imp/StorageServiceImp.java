package org.example.vegnbioapi.service.imp;

import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class StorageServiceImp implements StorageService {

    @Value("${aws.s3-bucket-name}")
    private String bucketName;
    @Autowired
    private S3Client s3Client;

    public List<String> uploadPictures(String directory, List<MultipartFile> pictures) {

        List<String> pictureUrls = new ArrayList<>();

        for (int index = 0; index < pictures.size(); index++) {
            String filename = "img_" + index + "." + Utils.getExtension(pictures.get(index).getOriginalFilename());
            String key = directory+"/" + Utils.generateFolderName() + "/" + filename;
            try {
                s3Client.putObject(
                        PutObjectRequest.builder()
                                .bucket(bucketName)
                                .key(key)
                                .contentType(pictures.get(index).getContentType())
                                .build(),
                        RequestBody.fromBytes(pictures.get(index).getBytes()));
                pictureUrls.add("https://" + bucketName + ".s3.amazonaws.com/" + key);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        return pictureUrls;
    }

    @Override
    public List<String> uploadPdf(String userId, List<MultipartFile> files) {
        List<String> fileUrls = new ArrayList<>();

        for (int index = 0; index < files.size(); index++) {
            String filename = "doc_" + userId + index + "." + Utils.getExtension(files.get(index).getOriginalFilename());
            String key = "restorer/" + Utils.generateFolderName() + "/" + filename;
            try {
                s3Client.putObject(
                        PutObjectRequest.builder()
                                .bucket(bucketName)
                                .key(key)
                                .contentType(files.get(index).getContentType())
                                .build(),
                        RequestBody.fromBytes(files.get(index).getBytes()));
                fileUrls.add("https://" + bucketName + ".s3.amazonaws.com/" + key);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        return fileUrls;
    }

    @Override
    public void deleteFromS3(List<String> pictureUrls) {
        if (pictureUrls == null) return;
        for (String url : pictureUrls) {
            String key = extractKeyFromUrl(url);
            try {
                s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build());
            } catch (S3Exception e) {
                log.warn("Erreur lors de la suppression de l'image S3 : {}", key, e);
            }
        }

    }

    @Override
    public String extractKeyFromUrl(String url) {
        URI uri = URI.create(url);
        return uri.getPath().startsWith("/") ? uri.getPath().substring(1) : uri.getPath();
    }

    /*@Override
    public List<String> savePictures(List<MultipartFile> pictures) throws IOException {
        List<String> imgUrls = new ArrayList<>();

        for (int index = 0; index < pictures.size(); index++) {
            String filename = "img_" + index + "." + Utils.getExtension(pictures.get(index).getOriginalFilename());
            String key = "menuItem/" + Utils.generateFolderName() + "/" + filename;
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(pictures.get(index).getContentType())
                            .build(),
                    RequestBody.fromBytes(pictures.get(index).getBytes()));
            imgUrls.add("https://" + bucketName + ".s3.amazonaws.com/" + key);
        }
        return imgUrls;
    }*/


}
