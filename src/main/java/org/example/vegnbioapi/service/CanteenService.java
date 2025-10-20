package org.example.vegnbioapi.service;

import org.example.vegnbioapi.dto.CanteenDto;
import org.example.vegnbioapi.dto.CanteenFilter;
import org.example.vegnbioapi.dto.ItemMenuFilter;
import org.example.vegnbioapi.model.Canteen;
import org.example.vegnbioapi.model.MenuItem;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;



public interface CanteenService {

    Canteen saveCanteen(CanteenDto canteenDto, List<MultipartFile> pictures) throws IOException;
    List<Canteen> getCanteenForCurrentUser(Principal principal, CanteenFilter filters);
    List<Canteen> loadFilteredCanteens(CanteenFilter filters);
    Canteen loadById(String id);
    Canteen delete(String id);

}
