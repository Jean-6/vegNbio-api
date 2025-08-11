package org.example.vegnbioapi.service.imp;


import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.EventDto;
import org.example.vegnbioapi.model.Event;
import org.example.vegnbioapi.repository.CustomEventRepo;
import org.example.vegnbioapi.repository.CustomEventRepoImp;
import org.example.vegnbioapi.repository.EventRepo;
import org.example.vegnbioapi.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class EventServiceImp implements EventService {

    @Value("${aws.s3-bucket-name}")
    private String bucketName;
    @Autowired
    private EventRepo eventRepo;
    @Autowired
    private CustomEventRepo customEventRepo;
    @Autowired
    private S3Client s3Client;

    @Override
    public Event saveEvent(EventDto eventDto, List<MultipartFile> pictures) throws IOException {

        List<String> pictureUrls = new ArrayList<>();

        for(int index = 0; index< pictures.size(); index++){
            String filename = "img_"+ index + "."+Utils.getExtension(pictures.get(index).getOriginalFilename());
            String key = "event/" + Utils.generateFolderName() + "/"+filename ;
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(pictures.get(index).getContentType())
                            .build(),
                    RequestBody.fromBytes(pictures.get(index).getBytes()));
            pictureUrls.add("https://" + bucketName + ".s3.amazonaws.com/" + key);
        }
        Event event = new Event();
        event.setRestaurantId("");
        event.setTitle(eventDto.getTitle());
        event.setDesc(eventDto.getDesc());
        event.setCategory(eventDto.getCategory());
        event.setLocation(eventDto.getLocation());
        event.setStartDate(eventDto.getStartDate());
        event.setEndDate(eventDto.getEndDate());
        event.setPictures(pictureUrls);
        eventRepo.save(event);
        return event;
    }

    @Override
    public List<Event> getEvents(String restaurantId, LocalDateTime startDate, LocalDateTime endDate) {
        return customEventRepo.findEventsWithFilters(restaurantId,startDate,endDate);
    }


}
