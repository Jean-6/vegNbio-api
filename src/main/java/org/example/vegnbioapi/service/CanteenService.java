package org.example.vegnbioapi.service;

import org.example.vegnbioapi.dto.*;
import org.example.vegnbioapi.model.Canteen;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;



public interface CanteenService {

    Canteen saveCanteen(AddCanteen canteenDto, List<MultipartFile> pictures) throws IOException;
    List<Canteen> getApprovedCanteens(CanteenFilter filters);
    List<Canteen> getApprovedCanteensForCurrentUser(Principal principal);
    List<Canteen> getCanteenForCurrentUser(Principal principal, CanteenFilter filters);
    List<Canteen> loadFilteredCanteens(CanteenFilter filters);
    Canteen loadById(String id);
    Canteen delete(String id);
    Canteen approveOrRejectCanteen(String canteenId, Approval request) ;

}
