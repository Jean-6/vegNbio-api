package org.example.vegnbioapi.repository;

import org.example.vegnbioapi.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomEventRepo {
    List<Event> findEventsWithFilters(String restaurantId, LocalDateTime startDate, LocalDateTime endDate);

}
