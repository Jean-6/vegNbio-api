package org.example.vegnbioapi.service;

import org.example.vegnbioapi.dto.AddEventDto;
import org.example.vegnbioapi.dto.EventDto;
import org.example.vegnbioapi.dto.EventFilter;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface EventService {



    List<EventDto> getApprovedEvents(EventFilter filters);
    List<org.example.vegnbioapi.model.Event> getEventForCurrentUser(Principal principal, EventFilter filters);
    org.example.vegnbioapi.model.Event saveEvent(AddEventDto addEventDto, List<MultipartFile> pictures) throws IOException;
    List<org.example.vegnbioapi.model.Event> loadFilteredEvents(EventFilter eventFilter) ;
    org.example.vegnbioapi.model.Event delete(String id);
    org.example.vegnbioapi.model.Event approveOrRejectEvent(String eventId, org.example.vegnbioapi.dto.Approval request);

}

