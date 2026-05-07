package mycode.onlineshopspring.auth.permission;

import lombok.RequiredArgsConstructor;
import mycode.onlineshopspring.auth.permission.dto.CreatePermissionRequest;
import mycode.onlineshopspring.auth.permission.dto.PermissionResponse;
import mycode.onlineshopspring.auth.permission.exceptions.PermissionAlreadyExistsException;
import mycode.onlineshopspring.auth.permission.exceptions.PermissionNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public List<PermissionResponse> findAll() {
        return permissionRepository.findAll().stream()
                .map(p -> new PermissionResponse(p.getId(), p.getName()))
                .toList();
    }

    @Transactional
    public PermissionResponse create(CreatePermissionRequest request) {
        String name = request.name().trim();
        if (permissionRepository.findByName(name).isPresent()) {
            throw new PermissionAlreadyExistsException(name);
        }
        Permission permission = new Permission();
        permission.setName(name);
        Permission saved = permissionRepository.save(permission);
        return new PermissionResponse(saved.getId(), saved.getName());
    }

    @Transactional
    public void delete(UUID id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new PermissionNotFoundException(id.toString()));
        permissionRepository.delete(permission);
    }
}
