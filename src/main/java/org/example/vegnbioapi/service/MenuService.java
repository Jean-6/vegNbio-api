package org.example.vegnbioapi.service;

import org.example.vegnbioapi.dto.MenuDto;
import org.example.vegnbioapi.model.Menu;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface MenuService {

    Menu saveMenu(MenuDto menuDto) throws IOException;
    Optional<Menu> loadById(String id);
    List<Menu> loadFilteredMenus(String restaurantId, String name,  String dietType);
    public void deleteMenu(String id);
}
