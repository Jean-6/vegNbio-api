package org.example.vegnbioapi.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.EventDto;
import org.example.vegnbioapi.dto.EventFilter;
import org.example.vegnbioapi.dto.ResponseWrapper;
import org.example.vegnbioapi.model.Event;
import org.example.vegnbioapi.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/event")
public class EventController {

    @Autowired
    private EventService eventService;


    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<Event>> save(
            @RequestPart("data") EventDto eventDto,
            @RequestPart("pictures") List<MultipartFile> pictures,
            HttpServletRequest request) throws IOException {

        log.info(">> Save an event ");
        log.debug(">> eventDto  : {}", eventDto.toString());
        Event newEvent = eventService.saveEvent(eventDto, pictures);
        return ResponseEntity.ok(
                ResponseWrapper.ok("event saved", request.getRequestURI(), newEvent));
    }


    @GetMapping(value="/", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<List <Event>>> getAllEvents(
            @ModelAttribute EventFilter filters,
            HttpServletRequest request) {

        log.info(">> Load filtered events  ");
        log.debug(">> Event filter DTO  : {}", filters);
        log.info(">> Get all Events ");
        List<Event> events = eventService.loadFilteredEvents(filters);
        return ResponseEntity.ok(
                ResponseWrapper.ok("offer saved", request.getRequestURI(), events));
    }

}
