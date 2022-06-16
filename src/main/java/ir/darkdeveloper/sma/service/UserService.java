package ir.darkdeveloper.sma.service;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ir.darkdeveloper.sma.dto.JwtAuth;
import ir.darkdeveloper.sma.model.Authority;
import ir.darkdeveloper.sma.model.UserModel;
import ir.darkdeveloper.sma.repository.UserRepo;
import ir.darkdeveloper.sma.utils.UserUtils;

@Service("userService")
public class UserService implements UserDetailsService {

    private final UserRepo repo;
    private final UserUtils userUtils;

    @Autowired
    public UserService(UserRepo repo, UserRolesService roleService, UserUtils userUtils) {
        this.repo = repo;
        this.userUtils = userUtils;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userUtils.loadUserByUsername(username);
    }

    @Transactional
    public UserModel updateUser(UserModel model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.getName().equals("anonymousUser") || auth.getAuthorities().contains(Authority.OP_ACCESS_ADMIN)
                || auth.getName().equals(model.getEmail())) {
            try {
                userUtils.validateUserData(model);
                return repo.save(model);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Transactional
    @PreAuthorize("authentication.name == this.getAdminUsername() || #user.getEmail() == authentication.name")
    public ResponseEntity<?> deleteUser(UserModel user) {
        try {
            UserModel model = repo.findUserById(user.getId());
            userUtils.deleteUser(model);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyAuthority('OP_ACCESS_ADMIN','OP_ACCESS_USER')")
    public Page<UserModel> allUsers(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public ResponseEntity<?> loginUser(JwtAuth model, HttpServletResponse response) {

        try {
            if (model.username().equals(userUtils.getAdminUsername())) {
                userUtils.authenticateUser(model, null, null, response);
            } else {
                userUtils.authenticateUser(model, userUtils.getUserIdByUsernameOrEmail(model.username()), null,
                        response);
            }
            return new ResponseEntity<>(repo.findByEmailOrUsername(model.username()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> signUpUser(UserModel model, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getName().equals("anonymousUser") || auth.getAuthorities().contains(Authority.OP_ACCESS_ADMIN)
                || !auth.getName().equals(model.getEmail())) {
            try {

                if (model.getUserName() != null && model.getUserName().equals(userUtils.getAdminUsername())) {
                    return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
                }
                String rawPass = model.getPassword();
                userUtils.validateUserData(model);
                repo.save(model);
                JwtAuth jwtAuth = new JwtAuth(model.getEmail(), model.getPassword());
                userUtils.authenticateUser(jwtAuth, model.getId(), rawPass, response);
                return new ResponseEntity<>(repo.findByEmailOrUsername(model.getUsername()), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PreAuthorize("hasAnyAuthority('OP_ACCESS_ADMIN','OP_ACCESS_USER')")
    public UserModel getUserInfo(UserModel model) {
        return repo.findUserById(model.getId());
    }

}
