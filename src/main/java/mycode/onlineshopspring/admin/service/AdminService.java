package mycode.onlineshopspring.admin.service;

import lombok.RequiredArgsConstructor;
import mycode.onlineshopspring.admin.dto.AdminResponse;
import mycode.onlineshopspring.admin.dto.CreateAdminRequest;
import mycode.onlineshopspring.admin.dto.UpdateAdminRequest;
import mycode.onlineshopspring.admin.exceptions.AdminNotFoundException;
import mycode.onlineshopspring.auth.admin.Admin;
import mycode.onlineshopspring.auth.admin.AdminRepository;
import mycode.onlineshopspring.auth.exceptions.EmailAlreadyUsedException;
import mycode.onlineshopspring.auth.permission.Permission;
import mycode.onlineshopspring.auth.permission.PermissionRepository;
import mycode.onlineshopspring.auth.permission.Permissions;
import mycode.onlineshopspring.auth.user.User;
import mycode.onlineshopspring.auth.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<AdminResponse> findAll() {
        return adminRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AdminResponse findById(UUID id) {
        return toResponse(load(id));
    }

    @Transactional(readOnly = true)
    public AdminResponse findByEmail(String email) {
        Admin admin = adminRepository.findByUserEmail(email).orElseThrow(AdminNotFoundException::new);
        return toResponse(admin);
    }

    @Transactional
    public AdminResponse create(CreateAdminRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyUsedException();
        }
        Set<Permission> all = new HashSet<>(permissionRepository.findByNameIn(
                new HashSet<>(Arrays.asList(Permissions.all()))
        ));

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .enabled(true)
                .permissions(all)
                .build();

        Admin admin = new Admin();
        admin.setUser(user);
        admin.setDisplayName(request.displayName());
        admin.setDepartment(request.department());
        admin.setNotes(request.notes());

        Admin saved = adminRepository.save(admin);
        return toResponse(saved);
    }

    @Transactional
    public AdminResponse update(UUID id, UpdateAdminRequest request) {
        Admin admin = load(id);
        return applyAndSave(admin, request);
    }

    @Transactional
    public AdminResponse updateByEmail(String email, UpdateAdminRequest request) {
        Admin admin = adminRepository.findByUserEmail(email).orElseThrow(AdminNotFoundException::new);
        return applyAndSave(admin, request);
    }

    @Transactional
    public void delete(UUID id) {
        Admin admin = load(id);
        adminRepository.delete(admin);
    }

    private AdminResponse applyAndSave(Admin admin, UpdateAdminRequest request) {
        admin.setDisplayName(request.displayName());
        admin.setDepartment(request.department());
        admin.setNotes(request.notes());
        Admin saved = adminRepository.save(admin);
        return toResponse(saved);
    }

    private Admin load(UUID id) {
        return adminRepository.findById(id).orElseThrow(AdminNotFoundException::new);
    }

    private AdminResponse toResponse(Admin admin) {
        String email = admin.getUser() != null ? admin.getUser().getEmail() : "";
        return new AdminResponse(admin.getId(), email, admin.getDisplayName(), admin.getDepartment(), admin.getNotes());
    }
}
