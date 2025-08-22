package org.example.vegnbioapi.service.imp;

import org.example.vegnbioapi.dto.RoomReservationDto;
import org.example.vegnbioapi.dto.TableReservationDto;
import org.example.vegnbioapi.model.Booking;
import org.example.vegnbioapi.model.BookingType;
import org.example.vegnbioapi.model.Canteen;
import org.example.vegnbioapi.repository.BookingRepo;
import org.example.vegnbioapi.repository.CanteenRepo;
import org.example.vegnbioapi.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Service
public class BookingServiceImp implements BookingService {

    @Autowired
    private BookingRepo bookingRepo;
    @Autowired
    private CanteenRepo canteenRepo;
    @Autowired
    private MongoTemplate mongoTemplate;


    public List<Booking> findConflict(String restaurantId, BookingType type,
                                      LocalDate date, LocalTime start, LocalTime end) {

        Criteria criteria = new Criteria().andOperator(
                Criteria.where("restaurantId").is(restaurantId),
                Criteria.where("type").is(type),
                Criteria.where("date").is(date),
                Criteria.where("startTime").lt(end),
                Criteria.where("endTime").gt(start)
        );

        Query query = new Query(criteria);
        return mongoTemplate.find(query, Booking.class);

    }

    @Override
    public Booking reserveTable(TableReservationDto booking) {

        Canteen canteen = canteenRepo.findById(booking.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Canteen not found"));

        List<Booking> conflicts = findConflict(
                booking.getRestaurantId(),
                BookingType.TABLE,
                booking.getDate(),
                booking.getStartTime(),
                booking.getEndTime()
        );

        int seatBooked = conflicts.stream()
                .mapToInt(b -> b.getNumberOfSeats() == null ? 0 : b.getNumberOfSeats())
                .sum();

        int requested = booking.getNumberOfSeats() == null ? 0 : booking.getNumberOfSeats();
        if (seatBooked + requested > canteen.getSeats()) {
            throw new RuntimeException("insufficient places ");
        }


        Booking newBooking = new Booking();
        newBooking.setRestaurantId(booking.getRestaurantId());
        newBooking.setType(BookingType.TABLE);
        newBooking.setStartTime(booking.getStartTime());
        newBooking.setEndTime(booking.getEndTime());
        newBooking.setDate(booking.getDate());
        newBooking.setNumberOfSeats(booking.getNumberOfSeats());
        //newBooking.setStatus();
        newBooking.setCreatedAt(LocalDateTime.from(Instant.now()));
        newBooking.setUserId(booking.getUserId());

        return bookingRepo.save(newBooking);
    }


    @Override
    public Booking reserveRoom(RoomReservationDto booking) {

        Canteen canteen = canteenRepo.findById(booking.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Canteen not found"));

        List<Booking> conflicts = findConflict(
                booking.getRestaurantId(),
                BookingType.ROOM,
                booking.getDate(),
                booking.getStartTime(),
                booking.getEndTime()
        );


        long roomsBooked = conflicts.size();
        if (roomsBooked >= canteen.getMeetingRooms()) {
            throw new RuntimeException("All rooms are already booked");
        }



        Booking newBooking = new Booking();
        newBooking.setRestaurantId(booking.getRestaurantId());
        newBooking.setType(BookingType.TABLE);
        newBooking.setStartTime(booking.getStartTime());
        newBooking.setEndTime(booking.getEndTime());
        newBooking.setDate(booking.getDate());
        //newBooking.setStatus();
        newBooking.setCreatedAt(LocalDateTime.from(Instant.now()));
        newBooking.setUserId(booking.getUserId());

        return bookingRepo.save(newBooking);
    }


}
