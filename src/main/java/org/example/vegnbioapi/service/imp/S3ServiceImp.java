package org.example.vegnbioapi.service.imp;

import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.service.S3Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class S3ServiceImp implements S3Storage {

    @Value("${aws.s3-bucket-name}")
    private String bucketName;
    @Autowired
    private S3Client s3Client;

    public List<String> upload(List<MultipartFile> pictures) {

        List<String> pictureUrls = new ArrayList<>();

        for (int index = 0; index < pictures.size(); index++) {
            String filename = "img_" + index + "." + Utils.getExtension(pictures.get(index).getOriginalFilename());
            String key = "dish/" + Utils.generateFolderName() + "/" + filename;
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


}
