package mycode.onlineshopspring.auth.user;

import lombok.RequiredArgsConstructor;
import mycode.onlineshopspring.auth.permission.Permission;
import mycode.onlineshopspring.auth.permission.PermissionRepository;
import mycode.onlineshopspring.auth.permission.exceptions.PermissionNotFoundException;
import mycode.onlineshopspring.auth.user.dto.UserResponse;
import mycode.onlineshopspring.auth.user.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(UUID userId) {
        return toResponse(loadUser(userId));
    }

    @Transactional(readOnly = true)
    public List<String> listPermissions(UUID userId) {
        return loadUser(userId).getPermissions().stream()
                .map(Permission::getName)
                .sorted()
                .toList();
    }

    @Transactional
    public List<String> assignPermission(UUID userId, String permissionName) {
        User user = loadUser(userId);
        Permission permission = permissionRepository.findByName(permissionName)
                .orElseThrow(() -> new PermissionNotFoundException(permissionName));
        user.getPermissions().add(permission);
        userRepository.save(user);
        return listSorted(user);
    }

    @Transactional
    public List<String> revokePermission(UUID userId, String permissionName) {
        User user = loadUser(userId);
        boolean removed = user.getPermissions().removeIf(p -> p.getName().equals(permissionName));
        if (!removed) {
            throw new PermissionNotFoundException(permissionName + " (not held by user)");
        }
        userRepository.save(user);
        return listSorted(user);
    }

    private User loadUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.isEnabled(), listSorted(user));
    }

    private List<String> listSorted(User user) {
        return user.getPermissions().stream()
                .map(Permission::getName)
                .sorted(Comparator.naturalOrder())
                .toList();
    }
}
