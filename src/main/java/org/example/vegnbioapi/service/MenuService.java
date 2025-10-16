package org.example.vegnbioapi.service;

import org.example.vegnbioapi.dto.AddMenuItem;
import org.example.vegnbioapi.dto.MenuDto;
import org.example.vegnbioapi.model.Menu;
import org.example.vegnbioapi.model.MenuItem;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface MenuService {

    MenuItem saveMenuItem(AddMenuItem menuItem, List<MultipartFile> pictures) throws IOException;
    List<String> savePictures(List<MultipartFile> pictures) throws IOException;
    Menu saveMenu(MenuDto menuDto) throws IOException;
    Optional<Menu> loadById(String id);
    List<Menu> loadFilteredMenus(String restaurantId, String name,  String dietType);
    void deleteMenu(String id);
    MenuItem deleteMenuItem(String id);
}
