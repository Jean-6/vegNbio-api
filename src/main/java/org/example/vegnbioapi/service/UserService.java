package org.example.vegnbioapi.service;


import org.example.vegnbioapi.dto.UserFilter;
import org.example.vegnbioapi.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {


    Boolean approve(String userId);
    List<User> loadFilteredUsers(UserFilter filters);
    List<String> saveRestorerDocs(String userId, List<MultipartFile> files);
    User delete(String id);
    User verifyUser(String id);
    User toggleActive(String id);
    User getUserById(String id);
}
