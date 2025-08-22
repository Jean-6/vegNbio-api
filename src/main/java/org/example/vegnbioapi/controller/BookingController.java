package org.example.vegnbioapi.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.*;
import org.example.vegnbioapi.model.Booking;
import org.example.vegnbioapi.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/booking")
public class BookingController {


    @Autowired
    private BookingService bookingService;


    @PostMapping(value = "/table", consumes =  MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<Booking>> reserveTable(
            @RequestBody TableReservationDto tableReservationDto,
            HttpServletRequest request)  {
        log.info(">> Save a table ");
        Booking  booking =  bookingService.reserveTable(tableReservationDto);
        return ResponseEntity.ok(
                ResponseWrapper.ok("table saved", request.getRequestURI(), booking));
    }

    @PostMapping(value = "/room", consumes =  MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<Booking>> reserveRoom(
            @RequestBody RoomReservationDto roomReservationDto,
            HttpServletRequest request)  {
        log.info(">> Save a room ");
        Booking  booking =  bookingService.reserveRoom(roomReservationDto);
        return ResponseEntity.ok(
                ResponseWrapper.ok("room saved", request.getRequestURI(), booking));
    }
}
