package org.example.vegnbioapi.service;


import org.example.vegnbioapi.dto.OfferDto;
import org.example.vegnbioapi.dto.OfferFilter;
import org.example.vegnbioapi.model.Offer;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OfferService {
    
    Offer save(OfferDto offerDto, List<MultipartFile> images);
    List<Offer> loadFilteredOffers( OfferFilter filters);

}
