package org.example.vegnbioapi.service;

import org.example.vegnbioapi.dto.CanteenDto;
import org.example.vegnbioapi.dto.CanteenFilter;
import org.example.vegnbioapi.model.Canteen;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;



public interface CanteenService {

    Canteen saveCanteen(CanteenDto canteenDto, List<MultipartFile> pictures) throws IOException;
    List<Canteen> loadFilteredCanteens(CanteenFilter filters);
    Canteen delete(String id);

}
