package org.example.vegnbioapi.service;


import org.example.vegnbioapi.dto.RoleChangeRequestDto;
import org.example.vegnbioapi.dto.SupplierRequest;
import org.example.vegnbioapi.dto.UserFilter;
import org.example.vegnbioapi.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface UserService {


    Boolean approve(String userId);
    List<RoleChangeRequestDto> getAllRoleChangeRequests();
    RoleChangeRequestDto approveRoleRequest(String requestId, String adminComment);
    User becomeRestorer(Principal principal, RoleChangeRequestDto request, List<MultipartFile> docs);
    List<User> loadFilteredUsers(UserFilter filters);
    User becomeSupplier(SupplierRequest request, List<MultipartFile> files) throws IOException;
    List<String> saveRestorerDocs(String userId, List<MultipartFile> files);
    User delete(String id);
    User verifyUser(String id);
    User toggleActive(String id);
    User getUserById(String id);
}
