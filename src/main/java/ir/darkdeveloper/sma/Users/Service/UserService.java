package ir.darkdeveloper.sma.Users.Service;

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

import ir.darkdeveloper.sma.Configs.Security.JWT.JwtAuth;
import ir.darkdeveloper.sma.Configs.Security.JWT.Crud.RefreshModel;
import ir.darkdeveloper.sma.Configs.Security.JWT.Crud.RefreshService;
import ir.darkdeveloper.sma.Users.Models.Authority;
import ir.darkdeveloper.sma.Users.Models.UserModel;
import ir.darkdeveloper.sma.Users.Repo.UserRepo;
import ir.darkdeveloper.sma.Utils.JwtUtils;
import ir.darkdeveloper.sma.Utils.UserUtils;

@Service("userService")
public class UserService implements UserDetailsService {

    private final UserRepo repo;
    private final RefreshService refreshService;
    private final UserUtils userUtils;
    private final JwtUtils jwtUtils;

    
    @Autowired
    public UserService( UserRepo repo, UserRolesService roleService,
            RefreshService refreshService, UserUtils userUtils, JwtUtils jwtUtils) {
        this.refreshService = refreshService;
        this.repo = repo;
        this.userUtils = userUtils;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userUtils.loadUserByUsername(username);
    }

    @Transactional
    public UserModel updateUser(UserModel model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.getName().equals("anonymousUser")
                || auth.getAuthorities().contains(Authority.OP_ACCESS_ADMIN)
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
        RefreshModel refreshModel;
        if (model.getUsername().equals(userUtils.getAdminUsername())) {
            refreshModel = refreshService.getRefreshByUserId(userUtils.getAdminId());
        } else {
            refreshModel = refreshService.getRefreshByUserId(userUtils.getUserIdByUsernameOrEmail(model.getUsername()));
        }

        if (refreshModel == null || !jwtUtils.isTokenExpired(refreshModel.getRefreshToken())) {
            try {
                if (model.getUsername().equals(userUtils.getAdminUsername())) {
                    userUtils.authenticateUser(model, null, null, response);
                } else {
                    userUtils.authenticateUser(model, userUtils.getUserIdByUsernameOrEmail(model.getUsername()), null,
                            response);
                }
                return new ResponseEntity<>(repo.findByEmailOrUsername(model.getUsername()), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>("Already logged in!", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> signUpUser(UserModel model, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getName().equals("anonymousUser")
                || auth.getAuthorities().contains(Authority.OP_ACCESS_ADMIN)
                || !auth.getName().equals(model.getEmail())) {
            try {
                
                if (model.getUserName() != null && model.getUserName().equals(userUtils.getAdminUsername())){
                    return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
                }
                String rawPass = model.getPassword();
                userUtils.validateUserData(model);
                repo.save(model);
                JwtAuth jwtAuth = new JwtAuth();
                jwtAuth.setUsername(model.getEmail());
                jwtAuth.setPassword(model.getPassword());
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
