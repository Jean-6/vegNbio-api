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

import java.security.Principal;
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
    public List<BookingView> getRestorerBookings(Principal principal, BookingFilter filters) {
        User restorer = userRepo.findUserByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé : " + principal.getName()));

        List<Canteen> canteens = canteenRepo.findCanteenByUserId(restorer.getId());
        if (canteens.isEmpty()) return List.of();

        List<String> canteenIds = canteens.stream().map(Canteen::getId).toList();
        List<BookingView> allBookings = new ArrayList<>();

        // --- TABLE BOOKINGS ---
        List<TableBooking> tableBookings = mongoTemplate.find(
                Query.query(Criteria.where("canteenInfo.canteenId").in(canteenIds)
                        .andOperator(
                                filters.getStartDate() != null ? Criteria.where("date").gte(filters.getStartDate()) : new Criteria(),
                                filters.getEndDate() != null ? Criteria.where("date").lte(filters.getEndDate()) : new Criteria()
                        )
                ), TableBooking.class
        );

        for (TableBooking t : tableBookings) {
            User bookingUser = userRepo.findById(t.getUserId()).orElse(null);
            allBookings.add(BookingView.builder()
                    .id(t.getId())
                    .type("TABLE")
                    .canteenInfo(t.getCanteenInfo())
                    .userInfo(bookingUser != null ? new UserInfo(bookingUser.getUsername(), bookingUser.getEmail()) : null)
                    .date(t.getDate())
                    .startTime(t.getStartTime())
                    .people(t.getPeople())
                    .createdAt(t.getCreatedAt())
                    .build()
            );
        }

        // --- ROOM BOOKINGS ---
        List<RoomBooking> roomBookings = mongoTemplate.find(
                Query.query(Criteria.where("canteenInfo.canteenId").in(canteenIds)
                        .andOperator(
                                filters.getStartDate() != null ? Criteria.where("date").gte(filters.getStartDate()) : new Criteria(),
                                filters.getEndDate() != null ? Criteria.where("date").lte(filters.getEndDate()) : new Criteria()
                        )
                ), RoomBooking.class
        );

        for (RoomBooking r : roomBookings) {
            User bookingUser = userRepo.findById(r.getUserId()).orElse(null);
            allBookings.add(BookingView.builder()
                    .id(r.getId())
                    .type("ROOM")
                    .canteenInfo(r.getCanteenInfo())
                    .userInfo(bookingUser != null ? new UserInfo(bookingUser.getUsername(), bookingUser.getEmail()) : null)
                    .date(r.getDate())
                    .startTime(r.getStartTime())
                    .endTime(r.getEndTime())
                    .people(r.getPeople())
                    .createdAt(r.getCreatedAt())
                    .build()
            );
        }

        // --- EVENT BOOKINGS ---
        List<EventBooking> eventBookings = mongoTemplate.findAll(EventBooking.class);
        for (EventBooking e : eventBookings) {
            // 1️⃣ Récupère l'utilisateur qui a réservé
            User bookingUser = userRepo.findById(e.getUserId()).orElse(null);

            // 2️⃣ Récupère l'événement pour avoir la date et les horaires
            Event event = eventRepo.findById(e.getEventId()).orElse(null);

            // 3️⃣ Récupère la cantine de l'événement si disponible
            CanteenInfo eventCanteenInfo = null;
            if (event != null && event.getCanteenId() != null) {
                Canteen canteen = canteenRepo.findById(event.getCanteenId()).orElse(null);
                if (canteen != null) {
                    eventCanteenInfo = new CanteenInfo(
                            canteen.getId(),
                            canteen.getName(),
                            canteen.getLocation(),
                            canteen.getContact()
                    );
                }
            }

            // 4️⃣ Ajoute la réservation dans la liste
            allBookings.add(BookingView.builder()
                    .id(e.getId())
                    .type("EVENT")
                    .userInfo(bookingUser != null ? new UserInfo(bookingUser.getUsername(), bookingUser.getEmail()) : null)
                    .canteenInfo(eventCanteenInfo)
                    .date(event != null && event.getDate() != null ? event.getDate() : null)
                    .startTime(event != null && event.getStartTime() != null ? event.getStartTime() : null)
                    .endTime(event != null && event.getEndTime() != null ? event.getEndTime() : null)     // HH:mm
                    .createdAt(e.getCreatedAt() != null ? e.getCreatedAt() : null)
                    .build()
            );
        }

        return allBookings;
    }


    @Override
    public List<Booking> getUserBookings(BookingFilter filters) {
        log.info(">> Fetching all user bookings");
        log.info(filters.toString());

        String userId = filters.getUserId();
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required to fetch user bookings");
        }

        String typeFilter = filters.getType();
        LocalDate startDate = filters.getStartDate();
        LocalDate endDate = filters.getEndDate();

        // Normaliser le type : null ou vide = tous types
        String typeFilterNormalized = typeFilter != null ? typeFilter.trim().toUpperCase() : "";

        List<Booking> userBookings = new ArrayList<>();

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        // --- EVENT BOOKINGS ---
        if (typeFilterNormalized.isEmpty() || typeFilterNormalized.equals("EVENEMENT")) {
            Query query = new Query().addCriteria(Criteria.where("userId").is(userId));
            if (startDate != null) query.addCriteria(Criteria.where("eventDate").gte(startDate));
            if (endDate != null) query.addCriteria(Criteria.where("eventDate").lte(endDate));

            List<EventBooking> eventBookings = mongoTemplate.find(query, EventBooking.class);

            for (EventBooking booking : eventBookings) {
                Event event = eventRepo.findById(booking.getEventId())
                        .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + booking.getEventId()));

                Canteen canteen = canteenRepo.findById(event.getCanteenId())
                        .orElseThrow(() -> new ResourceNotFoundException("Canteen not found: " + event.getCanteenId()));

                userBookings.add(Booking.builder()
                        .type("EVENT")
                        .startTime(event.getStartTime())
                        .endTime(event.getEndTime())
                        .date(event.getDate())
                        .people(0)
                        .userInfo(new UserInfo(user.getUsername(), user.getEmail()))
                        .canteenInfo(new CanteenInfo(canteen.getId(), canteen.getName(), canteen.getLocation(), canteen.getContact()))
                        .createdAt(booking.getCreatedAt())
                        .build());
            }
        }

        // --- TABLE BOOKINGS ---
        if (typeFilterNormalized.isEmpty() || typeFilterNormalized.equals("TABLE")) {
            Query query = new Query().addCriteria(Criteria.where("userId").is(userId));
            if (startDate != null) query.addCriteria(Criteria.where("date").gte(startDate));
            if (endDate != null) query.addCriteria(Criteria.where("date").lte(endDate));

            List<TableBooking> tableBookings = mongoTemplate.find(query, TableBooking.class);

            for (TableBooking booking : tableBookings) {
                Canteen canteen = canteenRepo.findById(booking.getCanteenInfo().getCanteenId())
                        .orElseThrow(() -> new ResourceNotFoundException("Canteen not found: " + booking.getCanteenInfo().getCanteenId()));

                userBookings.add(Booking.builder()
                        .type("TABLE")
                        .startTime(booking.getStartTime())
                        .date(booking.getDate())
                        .people(booking.getPeople())
                        .userInfo(new UserInfo(user.getUsername(), user.getEmail()))
                        .canteenInfo(new CanteenInfo(canteen.getId(), canteen.getName(), canteen.getLocation(), canteen.getContact()))
                        .createdAt(booking.getCreatedAt())
                        .build());
            }
        }

        // --- ROOM BOOKINGS ---
        if (typeFilterNormalized.isEmpty() || typeFilterNormalized.equals("SALLE")) {
            Query query = new Query().addCriteria(Criteria.where("userId").is(userId));
            if (startDate != null) query.addCriteria(Criteria.where("date").gte(startDate));
            if (endDate != null) query.addCriteria(Criteria.where("date").lte(endDate));

            List<RoomBooking> roomBookings = mongoTemplate.find(query, RoomBooking.class);

            for (RoomBooking booking : roomBookings) {
                Canteen canteen = canteenRepo.findById(booking.getCanteenInfo().getCanteenId())
                        .orElseThrow(() -> new ResourceNotFoundException("Canteen not found: " + booking.getCanteenInfo().getCanteenId()));

                userBookings.add(Booking.builder()
                        .type("ROOM")
                        .startTime(booking.getStartTime())
                        .endTime(booking.getEndTime())
                        .date(booking.getDate())
                        .people(booking.getPeople() != null ? booking.getPeople() : 0)
                        .userInfo(new UserInfo(user.getUsername(), user.getEmail()))
                        .canteenInfo(new CanteenInfo(canteen.getId(), canteen.getName(), canteen.getLocation(), canteen.getContact()))
                        .createdAt(booking.getCreatedAt())
                        .build());
            }
        }

        // --- Tri par date de création décroissante ---
        userBookings.sort((b1, b2) -> b2.getCreatedAt().compareTo(b1.getCreatedAt()));

        return userBookings;
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

        /*int seatBooked = conflicts.stream()
                .mapToInt(b -> b.getPeople() == null ? 0 : b.getPeople())
                .sum();*/

        TableBooking newBooking = new TableBooking();

        CanteenInfo canteenInfo = new CanteenInfo();
        canteenInfo.setCanteenId(canteen.getId());
        canteenInfo.setName(canteen.getName());
        canteenInfo.setLocation(canteen.getLocation());
        canteenInfo.setContact(canteen.getContact());

        newBooking.setCanteenInfo(canteenInfo);
        newBooking.setStartTime(booking.getStartTime());
        newBooking.setDate(booking.getDate());
        newBooking.setPeople(booking.getPeople());
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
        newBooking.setStartTime(booking.getStartTime());
        newBooking.setEndTime(booking.getEndTime());
        newBooking.setDate(booking.getDate());
        newBooking.setCreatedAt(LocalDateTime.now());
        newBooking.setUserId(booking.getUserId());
        newBooking.setRoomNumber(roomNumber);

        // Correctement remplir CanteenInfo depuis le canteen
        CanteenInfo canteenInfo = new CanteenInfo();
        canteenInfo.setCanteenId(canteen.getId());
        canteenInfo.setName(canteen.getName());
        canteenInfo.setLocation(canteen.getLocation());
        canteenInfo.setContact(canteen.getContact());

        newBooking.setCanteenInfo(canteenInfo);

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
    public List<RoomBooking> getRoomBookingsByCanteen(String canteenId) {
        return roomBookingRepo.findRoomBookingsByCanteenInfoCanteenId(canteenId);
    }


}
