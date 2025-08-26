package org.example.vegnbioapi.service;

import org.example.vegnbioapi.dto.RoomBookingDto;
import org.example.vegnbioapi.dto.TableBookingDto;
import org.example.vegnbioapi.model.RoomBooking;
import org.example.vegnbioapi.model.TableBooking;

public interface BookingService {

    TableBooking reserveTable(TableBookingDto booking) ;
    RoomBooking reserveRoom(RoomBookingDto booking) ;
}
