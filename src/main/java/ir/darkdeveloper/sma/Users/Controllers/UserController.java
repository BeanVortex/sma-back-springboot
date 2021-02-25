package ir.darkdeveloper.sma.Users.Controllers;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ir.darkdeveloper.sma.Configs.Security.JWT.JwtAuth;
import ir.darkdeveloper.sma.Configs.Security.JWT.JwtUtils;
import ir.darkdeveloper.sma.Users.Models.UserModel;
import ir.darkdeveloper.sma.Users.Service.UserService;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("*")
public class UserController {
    
    private final UserService service;
    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;

    @Autowired
    public UserController(UserService service, AuthenticationManager authManager, JwtUtils jwtUtils) {
        this.service = service;
        this.authManager = authManager;
        this.jwtUtils = jwtUtils;
    }
    


    @PostMapping("/signin/")
    public UserModel saveUser(@ModelAttribute UserModel user){
        return service.saveUser(user);
    }

    @PostMapping("/login/")
    public ResponseEntity<?> loginUser(@RequestBody JwtAuth model, HttpServletResponse response){
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(model.getUsername(), model.getPassword()));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        response.addHeader("Authorization", jwtUtils.generateToken(model.getUsername()));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteUser(@RequestBody UserModel user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(user.getUserName());
        System.out.println(authentication.getName());
        return service.deleteUser(user);
    }

    @GetMapping("/")
    public Page<UserModel> allUsers(Pageable pageable){
        return service.allUsers(pageable);
    }
}
