package org.example.vegnbioapi.service.imp;

import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.*;
import org.example.vegnbioapi.exception.ConflictException;
import org.example.vegnbioapi.model.*;
import org.example.vegnbioapi.model.User;
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
    private UserRepo userRepo;
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
        //newBooking.setName(booking.getName());
        newBooking.setStartTime(booking.getStartTime());
        newBooking.setDate(booking.getDate());
        newBooking.setPeople(booking.getPeople());
        //newBooking.setStatus();
        //newBooking.setUserId(booking.getUserId());
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
        //newBooking.setUserId(booking.getUserId());
        newBooking.setRoomNumber(roomNumber);

        return roomBookingRepo.save(newBooking);
    }

    @Override
    public EventBooking reserveEvent(AddEventBooking eventBooking) {

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
    public List<BookingDto> getReservations(BookingFilter filters ) {

        log.info("get all reservations");

        List<BookingDto> reservations = new ArrayList<>();
        String userId = filters.getUserId();

        // --- Events ---
        List<EventBooking> eventBookings = (userId == null || userId.isBlank())
                ? eventBookingRepo.findAll()
                : eventBookingRepo.findByUserId(userId);

        for (EventBooking booking : eventBookings) {
            Event event = eventRepo.findById(booking.getEventId())
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found : " + booking.getEventId()));

            Canteen canteen = canteenRepo.findById(event.getCanteenId())
                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found : " + event.getCanteenId()));

            User user = userRepo.findById(booking.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found : " + booking.getUserId()));

            BookingDto dto = new BookingDto(
                    "EVENT",
                    event.getStartTime(),
                    event.getEndTime(),
                    event.getDate(),
                    new UserInfo(user.getUsername(),user.getEmail()),
                    new CanteenInfo(canteen.getName(),canteen.getLocation(),canteen.getContact()),
                    booking.getCreatedAt()
            );

            reservations.add(dto);
        }

        // --- Réservations de tables ---
        List<TableBooking> tableBookings = (userId == null || userId.isBlank())
                ? tableBookingRepo.findAll()
                : tableBookingRepo.findByUserId(userId);
        for(TableBooking tableBooking : tableBookings){


            Canteen canteen = canteenRepo.findById(tableBooking.getCanteenId())
                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found : " + tableBooking.getCanteenId()));

            User user = userRepo.findById(tableBooking.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found : " + tableBooking.getUserId()));

            BookingDto dto = new BookingDto(
                                    "TABLE",
                                    tableBooking.getStartTime(),
                                    tableBooking.getDate(),
                                    tableBooking.getPeople(),
                                    new UserInfo(user.getUsername(),user.getEmail()),
                                    new CanteenInfo(canteen.getName(),canteen.getLocation(),canteen.getContact()),
                                    tableBooking.getCreatedAt()
                            );

            reservations.add(dto);


        }

        // --- ROOMS ---
        List<RoomBooking> roomBookings = (userId == null || userId.isBlank())
                ? roomBookingRepo.findAll()
                : roomBookingRepo.findByUserId(userId);
        for (RoomBooking roomBooking : roomBookings) {

            User user = userRepo.findById(roomBooking.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found : " + roomBooking.getUserId()));

            Canteen canteen = canteenRepo.findById(roomBooking.getCanteenId())
                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found : " + roomBooking.getCanteenId()));


            roomBookingRepo.findById(roomBooking.getId()).flatMap(
                    room -> canteenRepo.findById(room.getCanteenId())).ifPresent(canteenFounded -> {
                reservations.add(new BookingDto(
                        "ROOM",
                        roomBooking.getStartTime(),
                        roomBooking.getEndTime(),
                        roomBooking.getDate(),
                        new UserInfo(user.getUsername(),user.getEmail()),
                        new CanteenInfo(canteen.getName(),canteen.getLocation(),canteen.getContact()),
                        roomBooking.getCreatedAt()// date
                ));
            });
        }
        
        return reservations;
    }


}
