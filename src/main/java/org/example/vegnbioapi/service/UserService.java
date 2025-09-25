package org.example.vegnbioapi.service;


import org.example.vegnbioapi.dto.UserFilter;
import org.example.vegnbioapi.model.User;

import java.util.List;

public interface UserService {

    List<User> loadFilteredUsers(UserFilter filters);
}
