package org.example.vegnbioapi.service;

import org.example.vegnbioapi.dto.AddEventDto;
import org.example.vegnbioapi.dto.EventFilter;
import org.example.vegnbioapi.model.Event;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface EventService {

    Event saveEvent(AddEventDto addEventDto, List<MultipartFile> pictures) throws IOException;
    List<Event> loadFilteredEvents(EventFilter eventFilter) ;
    Event delete(String id);

}
