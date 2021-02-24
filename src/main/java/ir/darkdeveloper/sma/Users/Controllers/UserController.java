package ir.darkdeveloper.sma.Users.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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

import ir.darkdeveloper.sma.Users.Models.UserModel;
import ir.darkdeveloper.sma.Users.Service.UserService;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("*")
public class UserController {
    
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }
    


    @PostMapping("/")
    public UserModel saveUser(@ModelAttribute UserModel user){
        return service.saveUser(user);
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
