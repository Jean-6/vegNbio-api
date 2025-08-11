package org.example.vegnbioapi.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.EventDto;
import org.example.vegnbioapi.dto.ResponseWrapper;
import org.example.vegnbioapi.model.Event;
import org.example.vegnbioapi.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
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


    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<List<Event>>> get(
            @RequestParam(required = false) String restaurantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            HttpServletRequest request) {

        log.info(">> Load all Events");

        List<Event> events = eventService.getEvents(restaurantId, startDate, endDate);
        return ResponseEntity.ok(
                ResponseWrapper.ok("event list", request.getRequestURI(), events));
    }

}
