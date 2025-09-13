package org.example.vegnbioapi.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.*;
import org.example.vegnbioapi.model.Canteen;
import org.example.vegnbioapi.model.EventBooking;
import org.example.vegnbioapi.model.RoomBooking;
import org.example.vegnbioapi.model.TableBooking;
import org.example.vegnbioapi.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/booking")
@Validated
public class BookingController {


    @Autowired
    private BookingService bookingService;


    @GetMapping( produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<List<BookingDto>>> get(
            @Validated @ModelAttribute BookingFilter filters,
            HttpServletRequest request) {

        log.info(">> Load all reservations");

        List<BookingDto> reservations = bookingService.getUserReservations(filters);
        return ResponseEntity.ok(
                ResponseWrapper.ok("Reservation list", request.getRequestURI(), reservations));
    }

    @PostMapping(value = "/table", consumes =  MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<TableBooking>> reserveTable(
            @RequestBody TableBookingDto tableBookingDto,
            HttpServletRequest request)  {
        log.info(">> Save a booking table ");
        log.info(">> Table reservation DTO : {}", tableBookingDto);
        TableBooking booking =  bookingService.reserveTable(tableBookingDto);
        return ResponseEntity.ok(
                ResponseWrapper.ok("Reservation of table saved", request.getRequestURI(), booking));
    }

    @PostMapping(value = "/room", consumes =  MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<RoomBooking>> reserveRoom(
            @RequestBody RoomBookingDto roomBookingDto,
            HttpServletRequest request)  {
        log.info(">> Save a booking room ");
        log.info(">> Room reservation DTO : {}", roomBookingDto);
        RoomBooking booking =  bookingService.reserveRoom(roomBookingDto);
        return ResponseEntity.ok(
                ResponseWrapper.ok("Reservation of room saved", request.getRequestURI(), booking));
    }

    @PostMapping(value = "/event", consumes =  MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<EventBooking>> reserveEvent(
            @RequestBody EventBookingDto eventBooking ,
            HttpServletRequest request)  {
        log.info(">> Save a booking event ");
        log.info(">> Event reservation DTO : {}", eventBooking);
        EventBooking booking =  bookingService.reserveEvent(eventBooking);
        return ResponseEntity.ok(
                ResponseWrapper.ok("Reservation of event saved", request.getRequestURI(), booking));
    }


}
