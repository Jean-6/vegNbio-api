package org.example.vegnbioapi.service.imp;


import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.AddEventDto;
import org.example.vegnbioapi.dto.EventFilter;
import org.example.vegnbioapi.model.*;
import org.example.vegnbioapi.repository.CanteenRepo;
import org.example.vegnbioapi.repository.EventRepo;
import org.example.vegnbioapi.service.EventService;
import org.example.vegnbioapi.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.NotAcceptableStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class EventServiceImp implements EventService {

    @Value("${aws.s3-bucket-name}")
    private String bucketName;
    @Autowired
    private EventRepo eventRepo;
    @Autowired
    private S3Client s3Client;
    @Autowired
    private CanteenRepo canteenRepo;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private StorageService storageService;

    @Override
    public List<Canteen> getEventForCurrentUser(String username, EventFilter filters) {
        return List.of();
    }

    /**
     * public EventStatus computeStatus() {
     *     if (status == EventStatus.CANCELLED) {
     *         return EventStatus.CANCELLED;
     *     }
     *     LocalDateTime now = LocalDateTime.now();
     *     LocalDateTime start = LocalDateTime.of(date, startTime);
     *     LocalDateTime end = LocalDateTime.of(date, endTime);
     *     if (now.isBefore(start)) {
     *         return EventStatus.UPCOMING;
     *     } else if (!now.isAfter(end)) {
     *         return EventStatus.ONGOING;
     *     } else {
     *         return EventStatus.FINISHED;
     *     }
     * }
     */


    @Override
    public Event saveEvent(AddEventDto addEventDto, List<MultipartFile> pictures) throws IOException {

        Canteen canteen = canteenRepo.findById(addEventDto.getCanteenId())
                .orElseThrow(()-> new NotAcceptableStatusException("Canteen not found"));

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
        event.setCanteenId(addEventDto.getCanteenId());
        event.setTitle(addEventDto.getTitle());
        event.setDesc(addEventDto.getDesc());
        event.setType(addEventDto.getType());

        event.setLocation(canteen.getLocation());

        event.setStartTime(addEventDto.getStartTime());
        event.setEndTime(addEventDto.getEndTime());
        event.setDate(addEventDto.getDate());

        event.setStatus(EventStatus.UPCOMING);
        event.setPictures(pictureUrls);

        Approval app= new Approval();
        app.setStatus(Status.PENDING);
        event.setApproval(app);

        event.setCreatedAt(LocalDateTime.now());
        return  eventRepo.save(event);
    }

    @Override
    public List<Event> loadFilteredEvents(EventFilter eventFilter) {

        Query query = new Query();
        if(eventFilter.getCanteenId() != null && !eventFilter.getCanteenId().isEmpty()){
            query.addCriteria(Criteria.where("canteenId").is(eventFilter.getCanteenId()));
        }
        if(eventFilter.getType() != null ){
            query.addCriteria(Criteria.where("type").gte(eventFilter.getType()));
        }
        if(eventFilter.getStartDate() != null ){
            query.addCriteria(Criteria.where("startDate").gte(eventFilter.getStartDate()));
        }
        if(eventFilter.getEndDate() != null ){
            query.addCriteria(Criteria.where("date").lte(eventFilter.getEndDate()));
        }
        return mongoTemplate.find(query, Event.class);
    }

    @Override
    public Event delete(String id) {
        Optional<Event> eventOpt = eventRepo.findById(id);
        if (eventOpt.isEmpty()) {
            throw new ResourceNotFoundException("event not found with id : " + id);
        }
        Event event = eventOpt.get();
        storageService.deleteFromS3(event.getPictures());
        eventRepo.deleteById(event.getId());
        return  event;
    }


}
