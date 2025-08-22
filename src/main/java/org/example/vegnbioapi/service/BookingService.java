package org.example.vegnbioapi.service;

import org.example.vegnbioapi.dto.RoomReservationDto;
import org.example.vegnbioapi.dto.TableReservationDto;
import org.example.vegnbioapi.model.Booking;

public interface BookingService {

    Booking reserveTable(TableReservationDto booking) ;
    Booking reserveRoom(RoomReservationDto booking) ;
}
