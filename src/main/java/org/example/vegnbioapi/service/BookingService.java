package org.example.vegnbioapi.service;

import org.example.vegnbioapi.dto.*;
import org.example.vegnbioapi.model.EventBooking;
import org.example.vegnbioapi.model.RoomBooking;
import org.example.vegnbioapi.model.TableBooking;

import java.security.Principal;
import java.util.List;

public interface BookingService {




    List<BookingView> getAdminBookings(BookingFilter filters);
    List<BookingView> getRestorerBookings(Principal principal, BookingFilter filters) ;
    List<Booking> getUserBookings(BookingFilter filters) ;
    TableBooking reserveTable(TableBookingDto booking) ;
    RoomBooking reserveRoom(RoomBookingDto booking) ;
    EventBooking reserveEvent(AddEventBooking eventbooking) ;
    //List<BookingDto> getReservations(BookingFilter filters);
    List<RoomBooking> getRoomBookingsByCanteen(String canteenId);
}
