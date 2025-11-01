package org.example.vegnbioapi.service.imp;


import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.AddEventDto;
import org.example.vegnbioapi.dto.CanteenInfo;
import org.example.vegnbioapi.dto.EventDto;
import org.example.vegnbioapi.dto.EventFilter;
import org.example.vegnbioapi.model.*;
import org.example.vegnbioapi.repository.CanteenRepo;
import org.example.vegnbioapi.repository.EventRepo;
import org.example.vegnbioapi.repository.UserRepo;
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
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class EventServiceImp implements EventService {

    @Autowired
    private EventRepo eventRepo;
    @Autowired
    private CanteenRepo canteenRepo;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private StorageService storageService;
    @Autowired
    private UserRepo userRepo;


    private EventDto convertToDto(Event event) {
        EventDto dto = new EventDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDesc(event.getDesc());
        dto.setType(event.getType());
        dto.setStartTime(event.getStartTime());
        dto.setEndTime(event.getEndTime());
        dto.setDate(event.getDate());
        dto.setPictures(event.getPictures());
        dto.setCreatedAt(event.getCreatedAt());

        if (event.getCanteenId() != null) {
            CanteenInfo canteenInfo = canteenRepo
                    .findById(event.getCanteenId())
                    .map(c -> {
                        CanteenInfo info = new CanteenInfo();
                        info.setCanteenId(c.getId());
                        info.setName(c.getName());
                        info.setLocation(c.getLocation());
                        info.setContact(c.getContact());
                        return info;
                    })
                    .orElse(null);
            dto.setCanteenInfo(canteenInfo);
        }
        return dto;
    }


    @Override
    public List<EventDto> getApprovedEvents(EventFilter filters) {

        log.info("Filters: {}", filters);

        Criteria criteria = new Criteria();
        criteria.and("approval.status").is(Status.APPROVED);

        // 🔹 Filtrer par canteenId
        if (filters.getCanteenId() != null && !filters.getCanteenId().isEmpty()) {
            criteria.and("canteenId").is(filters.getCanteenId());
        }

        // 🔹 Filtrer par type
        if (filters.getType() != null && !filters.getType().isEmpty()) {
            criteria.and("type").is(filters.getType());
        }

        // 🔹 Filtrer par date (startDate et endDate)
        if (filters.getStartDate() != null || filters.getEndDate() != null) {
            Criteria dateCriteria = Criteria.where("date");
            if (filters.getStartDate() != null) dateCriteria = dateCriteria.gte(filters.getStartDate());
            if (filters.getEndDate() != null) dateCriteria = dateCriteria.lte(filters.getEndDate());
            criteria = criteria.andOperator(dateCriteria);
        }

        Query query = new Query(criteria);

        List<Event> events = mongoTemplate.find(query, Event.class);

        // 🔹 Transformer les Event → EventDto
        return events.stream().map(this::convertToDto).toList();
    }


    @Override
    public List<org.example.vegnbioapi.model.Event> getEventForCurrentUser(Principal principal, EventFilter filters) {

        User user = userRepo.findUserByUsername(principal.getName())
                .orElseThrow(()-> new ResourceNotFoundException("User not found with name:"+ principal.getName()));

        Criteria criteria = new Criteria();

        criteria.and("userId").is(user.getId());

        Query query = new Query(criteria);
        return mongoTemplate.find(query, org.example.vegnbioapi.model.Event.class);
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
    public org.example.vegnbioapi.model.Event saveEvent(AddEventDto addEventDto, List<MultipartFile> pictures) throws IOException {

        Canteen canteen = canteenRepo.findById(addEventDto.getCanteenId())
                .orElseThrow(()-> new ResourceNotFoundException("Canteen not found"));

        org.example.vegnbioapi.model.Event event = new org.example.vegnbioapi.model.Event();
        event.setCanteenId(addEventDto.getCanteenId());
        event.setTitle(addEventDto.getTitle());
        event.setDesc(addEventDto.getDesc());
        event.setType(addEventDto.getType());

        event.setLocation(canteen.getLocation());

        event.setStartTime(addEventDto.getStartTime());
        event.setEndTime(addEventDto.getEndTime());
        event.setDate(addEventDto.getDate());

        event.setStatus(EventStatus.UPCOMING);

        Approval app= new Approval();
        app.setStatus(Status.PENDING);
        event.setApproval(app);

        event.setCreatedAt(LocalDateTime.now());
        List<String> picturesUrl = this.storageService.uploadPictures("event",pictures);
        event.setPictures(picturesUrl);
        return  eventRepo.save(event);
    }

    @Override
    public List<org.example.vegnbioapi.model.Event> loadFilteredEvents(EventFilter eventFilter) {

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
        return mongoTemplate.find(query, org.example.vegnbioapi.model.Event.class);
    }

    @Override
    public org.example.vegnbioapi.model.Event delete(String id) {
        Optional<org.example.vegnbioapi.model.Event> eventOpt = eventRepo.findById(id);
        if (eventOpt.isEmpty()) {
            throw new ResourceNotFoundException("event not found with id : " + id);
        }
        org.example.vegnbioapi.model.Event event = eventOpt.get();
        storageService.deleteFromS3(event.getPictures());
        eventRepo.deleteById(event.getId());
        return  event;
    }

    @Override
    public org.example.vegnbioapi.model.Event approveOrRejectEvent(String eventId, org.example.vegnbioapi.dto.Approval request) {
        org.example.vegnbioapi.model.Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with : " + eventId));

        org.example.vegnbioapi.model.Approval approval = new org.example.vegnbioapi.model.Approval();
        approval.setStatus(request.getStatus());
        approval.setReasons(request.getReasons());
        approval.setDate(LocalDateTime.now());
        event.setApproval(approval);
        return eventRepo.save(event);
    }



}
