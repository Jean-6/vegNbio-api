package org.example.vegnbioapi.service.imp;

import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.RoomBookingDto;
import org.example.vegnbioapi.dto.TableBookingDto;
import org.example.vegnbioapi.exception.ConflictException;
import org.example.vegnbioapi.model.Booking;
import org.example.vegnbioapi.model.BookingType;
import org.example.vegnbioapi.model.Canteen;
import org.example.vegnbioapi.model.TableBooking;
import org.example.vegnbioapi.repository.BookingRepo;
import org.example.vegnbioapi.repository.CanteenRepo;
import org.example.vegnbioapi.repository.TableBookingRepo;
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
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;



@Slf4j
@Service
public class BookingServiceImp implements BookingService {

    @Autowired
    private BookingRepo bookingRepo;
    @Autowired
    private TableBookingRepo tableBookingRepo;
    @Autowired
    private CanteenRepo canteenRepo;
    @Autowired
    private MongoTemplate mongoTemplate;



    public List<Booking> findConflict(String restaurantId, BookingType type, LocalTime start, LocalTime end, LocalDate date) {

        Criteria criteria = new Criteria().andOperator(
                Criteria.where("restaurantId").is(restaurantId),
                Criteria.where("type").is(type),
                Criteria.where("startTime").lt(end),
                Criteria.where("endTime").gt(start),
                Criteria.where("date").is(date)
        );

        Query query = new Query(criteria);
        return mongoTemplate.find(query, Booking.class);

    }

    public List<Booking> findBookingTableConflict(String restaurantId, BookingType type, LocalTime start) {

        Criteria criteria = new Criteria().andOperator(
                Criteria.where("restaurantId").is(restaurantId),
                Criteria.where("type").is(type),
                Criteria.where("startTime").is(start)
        );
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Booking.class);

    }

    @Override
    public TableBooking reserveTable(TableBookingDto booking) {

        Canteen canteen = canteenRepo.findById(booking.getCanteenId())
                .orElseThrow(() -> new RuntimeException("Canteen not found"));

        List<Booking> conflicts = findBookingTableConflict(
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
        //newBooking.setType(BookingType.TABLE);
        newBooking.setStartTime(booking.getStartTime());
        newBooking.setDate(booking.getDate());
        newBooking.setPeople(booking.getPeople());
        //newBooking.setStatus();
        newBooking.setUserId(booking.getUserId());
        newBooking.setCreatedAt(LocalDateTime.now());

        return tableBookingRepo.save(newBooking);
    }


    @Override
    public Booking reserveRoom(RoomBookingDto booking) {

        Canteen canteen = canteenRepo.findById(booking.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Canteen not found"));

        // Conflits de réservation pour ce créneau
        List<Booking> conflicts = findConflict(
                booking.getRestaurantId(),
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

        Booking newBooking = new Booking();
        newBooking.setCanteenId(booking.getRestaurantId());
        newBooking.setType(BookingType.ROOM);
        newBooking.setStartTime(booking.getStartTime());
        newBooking.setEndTime(booking.getEndTime());
        newBooking.setDate(booking.getDate());
        newBooking.setCreatedAt(LocalDateTime.now());
        newBooking.setUserId(booking.getUserId());
        newBooking.setRoomNumber(roomNumber);

        return bookingRepo.save(newBooking);
    }


    private Integer assignRoomNumber(List<Booking> conflicts, int totalRooms) {
        Set<Integer> used = conflicts.stream()
                .map(Booking::getRoomNumber)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (int i = 1; i <= totalRooms; i++) {
            if (!used.contains(i)) {
                return i;
            }
        }
        throw new RuntimeException("Impossible d'attribuer un numéro de salle");
    }


}
