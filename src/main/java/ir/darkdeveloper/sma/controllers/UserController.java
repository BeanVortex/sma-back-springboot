package ir.darkdeveloper.sma.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ir.darkdeveloper.sma.dto.UserDto;
import ir.darkdeveloper.sma.dto.Mappers;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ir.darkdeveloper.sma.dto.LoginDto;
import ir.darkdeveloper.sma.model.UserModel;
import ir.darkdeveloper.sma.service.UserService;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final Mappers mappers;


    @PostMapping("/signup/")
    public ResponseEntity<UserDto> signUpUser(@ModelAttribute UserModel model, HttpServletResponse res) {
        var signedUpUser = userService.signUpUser(Optional.ofNullable(model), res);
        return new ResponseEntity<>(mappers.toDto(signedUpUser), HttpStatus.CREATED);
    }

    @PostMapping("/login/")
    public ResponseEntity<UserDto> loginUser(@RequestBody LoginDto model, HttpServletResponse res) {
        var loggedInUser = userService.loginUser(Optional.ofNullable(model), res);
        return ResponseEntity.ok(mappers.toDto(loggedInUser));
    }

    @PutMapping("/update/")
    @PreAuthorize("hasAuthority('OP_EDIT_USER')")
    public ResponseEntity<UserDto> updateUser(@ModelAttribute UserModel model, HttpServletRequest req) {
        var updatedUser = userService.updateUser(Optional.ofNullable(model), req);
        return ResponseEntity.ok(mappers.toDto(updatedUser));
    }

    @GetMapping("/all/")
    @PreAuthorize("hasAuthority('OP_ACCESS_USER')")
    public ResponseEntity<Page<UserDto>> findAll(Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(pageable).map(mappers::toDto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('OP_ACCESS_USER')")
    public ResponseEntity<UserDto> getUserInfo(@PathVariable Long id) {
        return ResponseEntity.ok(mappers.toDto(userService.getUserInfo(id)));
    }

    @DeleteMapping("/{id}/")
    @PreAuthorize("hasAuthority('OP_DELETE_USER')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id, HttpServletRequest req) {
        return ResponseEntity.ok(userService.deleteUser(id, req));
    }
}
