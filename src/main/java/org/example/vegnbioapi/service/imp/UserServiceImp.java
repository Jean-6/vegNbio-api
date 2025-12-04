package org.example.vegnbioapi.service.imp;


import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.*;
import org.example.vegnbioapi.model.*;
import org.example.vegnbioapi.model.Approval;
import org.example.vegnbioapi.model.User;
import org.example.vegnbioapi.repository.UserRepo;
import org.example.vegnbioapi.service.StorageService;
import org.example.vegnbioapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImp implements UserService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private StorageService storageService;

    @Override
    public Boolean approve(String userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //user.setVerified(true);
        userRepo.save(user);
        return true;
    }

    @Override
    public List<RoleChangeRequestDto> getAllRoleChangeRequests() {
        return userRepo.findAll().stream()
                .filter(user -> user.getRoleChangeRequest() != null)
                .map(user -> {
                    RoleChangeRequest req = user.getRoleChangeRequest();
                    RoleChangeRequestDto dto = new RoleChangeRequestDto();
                    dto.setUId(user.getId());
                    dto.setDesc(req.getDesc());
                    dto.setUserInfo(new UserInfo(user.getId(), user.getUsername(), user.getEmail()));
                    dto.setRequestedRole(req.getRequestedRole());
                    dto.setStatus(req.getStatus());
                    dto.setAdminComment(req.getAdminComment());
                    dto.setUrlDocs(req.getUrlDocs());
                    dto.setRequestedAt(req.getRequestedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }


    private RoleChangeRequestDto mapToDto(User user) {
        RoleChangeRequest request = user.getRoleChangeRequest();

        RoleChangeRequestDto dto = new RoleChangeRequestDto();
        dto.setUId(user.getId());
        dto.setUserInfo(new UserInfo(user.getUsername(), user.getEmail()));
        dto.setRequestedRole(request.getRequestedRole());
        dto.setRequestedAt(request.getRequestedAt());
        dto.setStatus(request.getStatus());
        dto.setDesc(request.getDesc());
        dto.setUrlDocs(request.getUrlDocs());

        return dto;
    }

    @Override
    public RoleChangeRequestDto approveRoleRequest(String userId, String adminComment) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        RoleChangeRequest request = user.getRoleChangeRequest();
        if (request == null) {
            throw new RuntimeException("Aucune demande de changement de rôle trouvée pour cet utilisateur");
        }

        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }

        ERole requestedRole = request.getRequestedRole();

        // Vérifier si l'utilisateur a déjà ce rôle
        boolean alreadyHasRole = user.getRoles().stream()
                .anyMatch(role -> role.getRole() == requestedRole);

        if (!alreadyHasRole) {
            user.getRoles().add(new Role(requestedRole));
        }

        // Selon le rôle demandé, créer les infos correspondantes
        Approval approval = new Approval();
        approval.setStatus(Status.APPROVED);
        approval.setReasons(adminComment);
        approval.setDate(LocalDateTime.now());

        if (requestedRole == ERole.RESTORER) {
            RestorerInfo restorerInfo = new RestorerInfo();
            restorerInfo.setDocsUrl(request.getUrlDocs());
            restorerInfo.setApproval(approval);
            user.setRestorerInfo(restorerInfo);
        } else if (requestedRole == ERole.SUPPLIER) {
            SupplierInfo supplierInfo = new SupplierInfo();
            supplierInfo.setDocsUrl(request.getUrlDocs());
            supplierInfo.setApproval(approval);
            user.setSupplierInfo(supplierInfo);
        }

        // Mettre à jour la demande de rôle
        request.setStatus(Status.APPROVED);
        request.setAdminComment(adminComment);
        user.setRoleChangeRequest(request);

        userRepo.save(user);

        RoleChangeRequestDto dto = new RoleChangeRequestDto();
        dto.setUId(user.getId());
        dto.setDesc(request.getDesc());
        dto.setRequestedRole(request.getRequestedRole());
        dto.setStatus(request.getStatus());
        dto.setAdminComment(request.getAdminComment());
        dto.setUrlDocs(request.getUrlDocs());
        dto.setRequestedAt(request.getRequestedAt());

        return dto;
    }

    @Override
    public User becomeRestorer(Principal principal,RoleChangeRequestDto dto, List<MultipartFile> docs) {

        User user = userRepo.findUserByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Créer la demande de rôle
        org.example.vegnbioapi.model.RoleChangeRequest req = new org.example.vegnbioapi.model.RoleChangeRequest();
        req.setRequestedRole(ERole.RESTORER);
        req.setStatus(Status.PENDING);
        req.setRequestedAt(LocalDateTime.now());

        // Ajouter userInfo
        UserInfo info = new UserInfo();
        info.setUserId(user.getId());
        info.setUsername(user.getUsername());
        info.setEmail(user.getEmail());
        req.setUserInfo(info);

        // Ajouter description si fourni
        req.setDesc(dto.getDesc());

        // Upload des documents
        List<String> docUrls = storageService.uploadPdf("", user.getId(), docs);
        req.setUrlDocs(docUrls);

        // Attacher la demande au user
        user.setRoleChangeRequest(req);

        // Sauvegarder l'utilisateur
        return userRepo.save(user);
    }

    @Override
    public List<User> loadFilteredUsers(UserFilter filters) {

        log.info(filters.toString());

        Criteria criteria = new Criteria();
        Criteria rolesCriteria = Criteria.where("roles.role").not().in(Set.of(ERole.ADMIN));

        if (filters.getRoles() != null && !filters.getRoles().isEmpty()) {
            rolesCriteria = new Criteria().andOperator(
                    Criteria.where("roles.role").not().in(Set.of(ERole.ADMIN)),
                    Criteria.where("roles.role").in(filters.getRoles())
            );
        }
        criteria.andOperator(rolesCriteria);

        if (filters.getEmail() != null && !filters.getEmail().isEmpty()) {
            criteria.and("email").regex(filters.getEmail(), "i");
        }

        if(filters.getIsActive()!=null && !filters.getIsActive().isEmpty()){
            if(filters.getIsActive().equals("verified")){
                criteria.and("isActive").is(true);
            }else{
                criteria.and("isActive").is(false);
            }
        }

        Query query = new Query(criteria);
        return mongoTemplate.find(query, User.class);
    }

    @Override
    public User becomeSupplier(SupplierRequest request, List<MultipartFile> files) throws IOException {


        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with Id : " + request.getUserId()));

        // Créer les infos de l'activité
        SupplierInfo supplierInfo = new SupplierInfo();
        supplierInfo.setCompanyName(request.getCompanyName());
        supplierInfo.setPhoneNumber(request.getPhone());

        // Upload des fichiers et récupération des URLs
        List<String> docUrls = storageService.uploadPdf(user.getId(), "supplier-docs", files);
        supplierInfo.setDocsUrl(docUrls);

        // Création de l'objet approval
        Approval approval = new Approval();
        approval.setStatus(Status.PENDING);
        approval.setDate(LocalDateTime.now());
        supplierInfo.setApproval(approval);
        supplierInfo.setSubmittedAt(LocalDateTime.now());

        // Rattacher les infos de l'activité au user
        user.setSupplierInfo(supplierInfo);

        // Créer la RoleChangeRequest pour SUPPLIER
        RoleChangeRequest roleRequest = new RoleChangeRequest();
        roleRequest.setRequestedRole(ERole.SUPPLIER);
        roleRequest.setRequestedAt(LocalDateTime.now());
        roleRequest.setUrlDocs(docUrls);
        roleRequest.setUserInfo(new UserInfo(user.getId(), user.getUsername(), user.getEmail()));
        roleRequest.setDesc(request.getDesc());
        roleRequest.setStatus(Status.PENDING);

        // Rattacher au user
        user.setRoleChangeRequest(roleRequest);

        // Sauvegarder l'utilisateur
        return userRepo.save(user);
    }

    /*public User becomeSupplier(SupplierRequest request, List<MultipartFile> files) throws IOException {

        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with Id : " + request.getUserId()));



        SupplierInfo supplierInfo = new SupplierInfo();
        supplierInfo.setCompanyName(request.getCompanyName());
        supplierInfo.setPhoneNumber(request.getPhone());
        //supplierInfo.setAddress(request.getAddress());

        Approval app = new Approval();
        app.setStatus(Status.PENDING);
        app.setDate(LocalDateTime.now());
        user.setSupplierInfo(supplierInfo);

        List<String> docUrls = storageService.uploadPdf(user.getId(), "supplier-docs",files);
        supplierInfo.setDocsUrl(docUrls);
        return userRepo.save(user);
    }*/

    @Override
    public List<String> saveRestorerDocs(String userId, List<MultipartFile> files) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with Id : " + userId));
        List<String> uploadedUrls = storageService.uploadPdf(user.getId(), files);

        /*if (user.getDocs() == null) {
            user.setDocs(new ArrayList<>());
        }
        user.getDocs().addAll(uploadedUrls);*/
        userRepo.save(user);
        return uploadedUrls;
    }

    public User verifyUser(String id) {
        User user = getUserById(id);
        //user.setVerified(true);
        return userRepo.save(user);
    }

    public User getUserById(String id) {
        return userRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found with id:"+ id));
    }

    public User toggleActive(String id) {
        User user = getUserById(id);
        user.setActive(!user.isActive());
        userRepo.save(user);
        return userRepo.save(user);
    }

    @Override
    public User delete(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        User userDeleted =  mongoTemplate.findAndRemove(query, User.class);
        assert userDeleted != null;
        log.info("deleted : "+ userDeleted.getId());
        return userDeleted;
    }
}
