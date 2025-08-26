package org.example.vegnbioapi.service;

import org.example.vegnbioapi.dto.RoomBookingDto;
import org.example.vegnbioapi.dto.TableBookingDto;
import org.example.vegnbioapi.model.Booking;
import org.example.vegnbioapi.model.TableBooking;

public interface BookingService {

    TableBooking reserveTable(TableBookingDto booking) ;
    Booking reserveRoom(RoomBookingDto booking) ;
}
