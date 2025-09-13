package org.example.vegnbioapi.service.imp;

import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.*;
import org.example.vegnbioapi.exception.ConflictException;
import org.example.vegnbioapi.model.*;
import org.example.vegnbioapi.repository.*;
import org.example.vegnbioapi.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;



@Slf4j
@Service
public class BookingServiceImp implements BookingService {

    @Autowired
    private TableBookingRepo tableBookingRepo;
    @Autowired
    private CanteenRepo canteenRepo;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RoomBookingRepo roomBookingRepo;
    @Autowired
    private EventBookingRepo eventBookingRepo ;
    @Autowired
    private EventRepo eventRepo ;


    public List<RoomBooking> findBookingRoomConflict(String restaurantId, BookingType type, LocalTime start, LocalTime end, LocalDate date) {

        Criteria criteria = new Criteria().andOperator(
                Criteria.where("restaurantId").is(restaurantId),
                Criteria.where("type").is(type),
                Criteria.where("startTime").lt(end),
                Criteria.where("endTime").gt(start),
                Criteria.where("date").is(date)
        );

        Query query = new Query(criteria);
        return mongoTemplate.find(query, RoomBooking.class);

    }

    public List<TableBooking> findBookingTableConflict(String restaurantId, BookingType type, LocalTime start) {

        Criteria criteria = new Criteria().andOperator(
                Criteria.where("restaurantId").is(restaurantId),
                Criteria.where("type").is(type),
                Criteria.where("startTime").is(start)
        );
        Query query = new Query(criteria);
        return mongoTemplate.find(query, TableBooking.class);

    }

    @Override
    public TableBooking reserveTable(TableBookingDto booking) {

        Canteen canteen = canteenRepo.findById(booking.getCanteenId())
                .orElseThrow(() -> new RuntimeException("Canteen not found"));

        List<TableBooking> conflicts = findBookingTableConflict(
                booking.getCanteenId(),
                BookingType.TABLE,
                booking.getStartTime()
        );

        int seatBooked = conflicts.stream()
                .mapToInt(b -> b.getPeople() == null ? 0 : b.getPeople())
                .sum();

        int requested = booking.getPeople() == null ? 0 : booking.getPeople();
        if (seatBooked + requested > canteen.getSeats()) {
            throw new RuntimeException("insufficient places ");
        }

        TableBooking newBooking = new TableBooking();
        newBooking.setCanteenId(booking.getCanteenId());
        newBooking.setName(booking.getName());
        newBooking.setStartTime(booking.getStartTime());
        newBooking.setDate(booking.getDate());
        newBooking.setPeople(booking.getPeople());
        //newBooking.setStatus();
        newBooking.setUserId(booking.getUserId());
        newBooking.setCreatedAt(LocalDateTime.now());

        return tableBookingRepo.save(newBooking);
    }


    @Override
    public RoomBooking reserveRoom(RoomBookingDto booking) {

        Canteen canteen = canteenRepo.findById(booking.getCanteenId())
                .orElseThrow(() -> new ResourceNotFoundException("Canteen not found"));

        // Conflits de réservation pour ce créneau
        List<RoomBooking> conflicts = findBookingRoomConflict(
                booking.getCanteenId(),
                BookingType.ROOM,
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getDate()
        );

        long roomsBooked = conflicts.size();

        // Vérifie si toutes les salles sont déjà prises
        if (roomsBooked >= canteen.getMeetingRooms()) {
            throw new ConflictException("All meeting rooms are already booked for this time slot");
        }

        // Assigne un numéro de salle disponible
        Integer roomNumber = assignRoomNumber(conflicts, canteen.getMeetingRooms());

        RoomBooking newBooking = new RoomBooking();
        newBooking.setCanteenId(booking.getCanteenId());
        newBooking.setStartTime(booking.getStartTime());
        newBooking.setEndTime(booking.getEndTime());
        newBooking.setDate(booking.getDate());
        newBooking.setCreatedAt(LocalDateTime.now());
        newBooking.setUserId(booking.getUserId());
        newBooking.setRoomNumber(roomNumber);

        return roomBookingRepo.save(newBooking);
    }

    @Override
    public EventBooking reserveEvent(EventBookingDto eventBooking) {

        EventBooking newEventBooking = new EventBooking();
        newEventBooking.setEventId(eventBooking.getEventId());
        newEventBooking.setUserId(eventBooking.getUserId());
        newEventBooking.setCreatedAt(LocalDateTime.now());
        return eventBookingRepo.save(newEventBooking);
    }

    private Integer assignRoomNumber(List<RoomBooking> conflicts, int totalRooms) {
        Set<Integer> used = conflicts.stream()
                .map(RoomBooking::getRoomNumber)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (int i = 1; i <= totalRooms; i++) {
            if (!used.contains(i)) {
                return i;
            }
        }
        throw new RuntimeException("Impossible d'attribuer un numéro de salle");
    }

    @Override
    public List<BookingDto> getUserReservations(BookingFilter filters ) {

        if(filters.getUserId() == null || filters.getUserId().isBlank()){
            throw new IllegalArgumentException("user id is required");
        }

        List<BookingDto> reservations = new ArrayList<>();

        // --- Events ---
        List<EventBooking> eventBookings = eventBookingRepo.findByUserId(filters.getUserId());
        for(EventBooking b : eventBookings){
            eventRepo.findById(b.getEventId()).ifPresent(
                    event -> {
                        String canteenName = canteenRepo.findById(event.getCanteenId())
                                .map(Canteen::getName)
                                .orElse("Inconnu");
                        reservations.add(new BookingDto(
                                event.getTitle(),
                                "EVENT",
                                canteenName,
                                event.getLocation(),
                                event.getStartTime(),
                                event.getEndTime(),
                                event.getDate()

                        ));
                    }
            );

        }

        // --- Réservations de tables ---
        List<TableBooking> tableBookings = tableBookingRepo.findByUserId(filters.getUserId());
        for(TableBooking b : tableBookings){
            tableBookingRepo.findById(b.getId()).ifPresent(
                    table -> {
                        Optional<Canteen> canteen= canteenRepo.findById(table.getCanteenId());
                        canteen.ifPresent(c->{
                            reservations.add(new BookingDto(
                                    "TABLE",
                                    c.getName(),
                                    c.getLocation(),
                                    b.getStartTime(),
                                    b.getDate()
                            ));

                        });

            });
        }

        // --- ROOMS ---
        List<RoomBooking> roomBookings = roomBookingRepo.findByUserId(filters.getUserId());
        for (RoomBooking b : roomBookings) {
            roomBookingRepo.findById(b.getId()).ifPresent(room -> {
                canteenRepo.findById(room.getCanteenId()).ifPresent(canteen -> {
                    reservations.add(new BookingDto(
                            room.getName(),          // title
                            "ROOM",                  // type
                            canteen.getName(),       // canteenName
                            canteen.getLocation(),   // location from restaurant
                            b.getStartTime(),        // startTime
                            b.getEndTime(),          // endTime
                            b.getDate()              // date
                    ));
                });
            });
        }
        
        return reservations;
    }


}
