package ir.darkdeveloper.sma.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ir.darkdeveloper.sma.dto.UserDto;
import ir.darkdeveloper.sma.dto.mapper.UserMapper;
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
    private final UserMapper userMapper;


    @PostMapping("/signup/")
    public ResponseEntity<UserDto> signUpUser(@ModelAttribute UserModel model, HttpServletResponse res) {
        var signedUpUser = userService.signUpUser(Optional.ofNullable(model), res);
        return new ResponseEntity<>(userMapper.toDto(signedUpUser), HttpStatus.CREATED);
    }

    @PostMapping("/login/")
    public ResponseEntity<UserDto> loginUser(@RequestBody LoginDto model, HttpServletResponse res) {
        var loggedInUser = userService.loginUser(Optional.ofNullable(model), res);
        return ResponseEntity.ok(userMapper.toDto(loggedInUser));
    }

    @PostMapping("/update/")
    @PreAuthorize("hasAnyAuthority('OP_ACCESS_ADMIN','OP_ACCESS_USER')")
    public ResponseEntity<UserDto> updateUser(@ModelAttribute UserModel model, HttpServletRequest req) {
        var updatedUser = userService.updateUser(Optional.ofNullable(model), req);
        return ResponseEntity.ok(userMapper.toDto(updatedUser));
    }


    @DeleteMapping("/{id}/")
    @PreAuthorize("hasAnyAuthority('OP_ACCESS_ADMIN','OP_ACCESS_USER')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id, HttpServletRequest req) {
        return ResponseEntity.ok(userService.deleteUser(id, req));
    }

    @GetMapping("/all/")
    @PreAuthorize("hasAnyAuthority('OP_ACCESS_ADMIN','OP_ACCESS_USER')")
    public ResponseEntity<Page<UserDto>> findAll(Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(pageable).map(userMapper::toDto));
    }

    @GetMapping("/")
    @PreAuthorize("hasAnyAuthority('OP_ACCESS_ADMIN','OP_ACCESS_USER')")
    public ResponseEntity<UserDto> getUserInfo(@RequestBody UserModel model) {
        return ResponseEntity.ok(userMapper.toDto(userService.getUserInfo(model)));
    }
}
