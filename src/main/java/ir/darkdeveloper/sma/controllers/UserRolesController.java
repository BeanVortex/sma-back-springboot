package ir.darkdeveloper.sma.controllers;

import ir.darkdeveloper.sma.model.UserRoles;
import ir.darkdeveloper.sma.service.UserRolesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/role")
@RequiredArgsConstructor
public class UserRolesController {

    private final UserRolesService service;

    @GetMapping("/")
    @PreAuthorize("hasAuthority('OP_ACCESS_ROLE')")
    public List<UserRoles> getAllRoles() {
        return service.getAllRoles();
    }

    @PostMapping("/")
    @PreAuthorize("hasAuthority('OP_ADD_ROLE')")
    public ResponseEntity<UserRoles> saveRole(@RequestBody UserRoles role) {
        return ResponseEntity.ok(service.saveRole(role));
    }

    @DeleteMapping("/{id}/")
    @PreAuthorize("hasAuthority('OP_DELETE_ROLE')")
    public ResponseEntity<String> deleteRole(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.deleteRole(id));
    }
}
