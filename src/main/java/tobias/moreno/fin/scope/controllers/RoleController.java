package tobias.moreno.fin.scope.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tobias.moreno.fin.scope.dto.roles.RoleResponseDTO;
import tobias.moreno.fin.scope.entities.RoleEntity;
import tobias.moreno.fin.scope.entities.UserEntity;
import tobias.moreno.fin.scope.services.RoleServiceImpl;
import tobias.moreno.fin.scope.services.UserServiceImpl;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Endpoints para gestión de roles y permisos de usuarios")
@SecurityRequirement(name = "Bearer Authentication")
public class RoleController {

    private final RoleServiceImpl roleService;
    private final UserServiceImpl userService;

    @Operation(
            summary = "Obtener todos los roles",
            description = "Retorna la lista completa de roles disponibles en el sistema",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de roles obtenida exitosamente",
                            content = @Content(schema = @Schema(implementation = RoleResponseDTO.class))),
                    @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
                    @ApiResponse(responseCode = "403", description = "Prohibido - Se requiere ADMIN_ACCESS")
            }
    )
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN_ACCESS')")
    public ResponseEntity<List<RoleResponseDTO>> getAllRoles() {
        List<RoleResponseDTO> roles = roleService.getAllRoles().stream()
                .map(RoleResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(roles);
    }

    @Operation(
            summary = "Obtener rol por nombre",
            description = "Retorna un rol específico por su nombre",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rol encontrado exitosamente",
                            content = @Content(schema = @Schema(implementation = RoleResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Rol no encontrado"),
                    @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
                    @ApiResponse(responseCode = "403", description = "Prohibido - Se requiere ADMIN_ACCESS")
            }
    )
    @GetMapping("/{name}")
    @PreAuthorize("hasAuthority('ADMIN_ACCESS')")
    public ResponseEntity<RoleResponseDTO> getRoleByName(@PathVariable String name) {
        return roleService.getRoleByName(name)
                .map(RoleResponseDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Actualizar usuario a Premium",
            description = "Actualiza el rol de un usuario específico a PREMIUM",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario actualizado a Premium exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Error al actualizar usuario"),
                    @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
                    @ApiResponse(responseCode = "403", description = "Prohibido - Se requiere USER_MANAGEMENT")
            }
    )
    @PostMapping("/users/{userId}/upgrade-to-premium")
    @PreAuthorize("hasAuthority('USER_MANAGEMENT')")
    public ResponseEntity<Map<String, String>> upgradeUserToPremium(@PathVariable Long userId) {
        boolean success = roleService.upgradeUserToPremium(userId);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "User upgraded to PREMIUM successfully"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to upgrade user to PREMIUM"));
        }
    }

    @Operation(
            summary = "Degradar usuario a Default",
            description = "Actualiza el rol de un usuario específico a DEFAULT",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario degradado a Default exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Error al degradar usuario"),
                    @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
                    @ApiResponse(responseCode = "403", description = "Prohibido - Se requiere USER_MANAGEMENT")
            }
    )
    @PostMapping("/users/{userId}/downgrade-to-default")
    @PreAuthorize("hasAuthority('USER_MANAGEMENT')")
    public ResponseEntity<Map<String, String>> downgradeUserToDefault(@PathVariable Long userId) {
        boolean success = roleService.downgradeUserToDefault(userId);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "User downgraded to DEFAULT successfully"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to downgrade user to DEFAULT"));
        }
    }

    @Operation(
            summary = "Obtener roles del usuario actual",
            description = "Retorna los roles del usuario autenticado actualmente",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Roles del usuario obtenidos exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Error al obtener roles"),
                    @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido")
            }
    )
    @GetMapping("/current-user/roles")
    public ResponseEntity<Map<String, Object>> getCurrentUserRoles() {
        try {
            UserEntity currentUser = userService.getUserFromContext();
            List<String> roleNames = currentUser.getRoles().stream()
                    .map(RoleEntity::getName)
                    .toList();
            
            return ResponseEntity.ok(Map.of(
                    "userId", currentUser.getId(),
                    "email", currentUser.getEmail(),
                    "roles", roleNames
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Obtener permisos del usuario actual",
            description = "Retorna los permisos del usuario autenticado actualmente",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Permisos del usuario obtenidos exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Error al obtener permisos"),
                    @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido")
            }
    )
    @GetMapping("/current-user/permissions")
    public ResponseEntity<Map<String, Object>> getCurrentUserPermissions() {
        try {
            UserEntity currentUser = userService.getUserFromContext();
            List<String> permissions = currentUser.getRoles().stream()
                    .flatMap(role -> role.getPermissions().stream())
                    .map(permission -> permission.getDescription())
                    .distinct()
                    .toList();
            
            return ResponseEntity.ok(Map.of(
                    "userId", currentUser.getId(),
                    "email", currentUser.getEmail(),
                    "permissions", permissions
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
