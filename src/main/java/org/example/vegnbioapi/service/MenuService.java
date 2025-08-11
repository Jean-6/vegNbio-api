package org.example.vegnbioapi.service;

import org.example.vegnbioapi.dto.MenuDto;
import org.example.vegnbioapi.model.Menu;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MenuService {

    Menu saveMenu(MenuDto menuDto) throws IOException;
    //List<Menu> getMenus(String restaurantId, LocalDateTime startDate, LocalDateTime endDate) ;
    Optional<Menu> loadById(String id);
    List<Menu> loadAll();
}
