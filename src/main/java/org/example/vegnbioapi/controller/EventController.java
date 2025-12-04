package org.example.vegnbioapi.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.*;
import org.example.vegnbioapi.model.Event;
import org.example.vegnbioapi.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/event")
@Tag(name = "Event Controller", description = "Manages CRUD operations on events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<Event>> save(
            @RequestPart("data") AddEventDto eventDto,
            @RequestPart("pictures") List<MultipartFile> pictures,
            HttpServletRequest request) throws IOException {

        log.info(">> Save an event ");
        log.debug(">> eventDto  : {}", eventDto.toString());
        Event newEvent = eventService.saveEvent(eventDto, pictures);
        return ResponseEntity.ok(
                ResponseWrapper.ok("event saved", request.getRequestURI(), newEvent));
    }

    @GetMapping(value="/all/approved" ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<List<EventDto>>> getApprovedCanteens(
            @ModelAttribute EventFilter filters,
            HttpServletRequest hsr) {
        log.info(">> Load all events approved ");
        List<EventDto> approvedEvent = eventService.getApprovedEvents(filters);
        return ResponseEntity.ok(
                ResponseWrapper.ok("Event list", hsr.getRequestURI(), approvedEvent));
    }



    /**/
    @GetMapping(value="/me", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<List <Event>>> getAllEvents(
            @ModelAttribute EventFilter filters,
            Principal principal,
            HttpServletRequest hsr) {

        log.info(">> Load filtered events  ");
        log.debug(">> Event filter DTO  : {}", filters);
        log.info(">> Get all Events ");
        List<Event> events = eventService.getEventForCurrentUser(principal,filters);
        return ResponseEntity.ok(
                ResponseWrapper.ok("Event list for current user", hsr.getRequestURI(), events));
    }

    @GetMapping(value="/", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<List <Event>>> getAllEvents(
            @ModelAttribute EventFilter filters,
            HttpServletRequest hsr) {

        log.info(">> Load filtered events  ");
        log.debug(">> Event filter DTO  : {}", filters);
        log.info(">> Get all Events ");
        List<Event> events = eventService.loadFilteredEvents(filters);
        return ResponseEntity.ok(
                ResponseWrapper.ok("Event list", hsr.getRequestURI(), events));
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<Event>> delete(
            @PathVariable("id") String id,
            HttpServletRequest request)  {

        log.info("id : "+ id);

        log.info(">> Delete a event ");
        Event event =  eventService.delete(id);
        return ResponseEntity.ok(
                ResponseWrapper.ok("Event deleted", request.getRequestURI(), event));
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<ResponseWrapper<Event>> approveOrRejectCanteen(
            @PathVariable String id,
            @RequestBody Approval request,
            HttpServletRequest hsr) {
        Event updated = eventService.approveOrRejectEvent(id, request);
        return ResponseEntity.ok(
                ResponseWrapper.ok("Event approved", hsr.getRequestURI(), updated));
    }

}
