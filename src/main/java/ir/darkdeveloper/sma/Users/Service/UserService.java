package ir.darkdeveloper.sma.Users.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import ir.darkdeveloper.sma.Configs.Security.JWT.JwtAuth;
import ir.darkdeveloper.sma.Configs.Security.JWT.JwtUtils;
import ir.darkdeveloper.sma.Configs.Security.JWT.Crud.RefreshModel;
import ir.darkdeveloper.sma.Configs.Security.JWT.Crud.RefreshService;
import ir.darkdeveloper.sma.Users.Models.Authority;
import ir.darkdeveloper.sma.Users.Models.UserModel;
import ir.darkdeveloper.sma.Users.Repo.UserRepo;

@Service("userService")
public class UserService implements UserDetailsService {

    @Value("${spring.security.user.name}")
    private String adminUsername;

    @Value("${spring.security.user.password}")
    private String adminPassword;

    private final Long adminId = -1L;

    private final PasswordEncoder encoder;
    private final UserRepo repo;
    private final UserRolesService roleService;
    private final RefreshService refreshService;
    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;

    @Autowired
    public UserService(@Lazy PasswordEncoder encoder, UserRepo repo, UserRolesService roleService,
            RefreshService refreshService, AuthenticationManager authManager, JwtUtils jwtUtils) {
        this.refreshService = refreshService;
        this.authManager = authManager;
        this.jwtUtils = jwtUtils;
        this.repo = repo;
        this.encoder = encoder;
        this.roleService = roleService;

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.equals(adminUsername)) {
            GrantedAuthority[] authorities = { Authority.OP_ACCESS_ADMIN, Authority.OP_EDIT_ADMIN,
                    Authority.OP_ADD_ADMIN, Authority.OP_DELETE_ADMIN, Authority.OP_ACCESS_USER, Authority.OP_EDIT_USER,
                    Authority.OP_DELETE_USER, Authority.OP_ADD_USER, Authority.OP_ADD_ROLE, Authority.OP_DELETE_ROLE,
                    Authority.OP_ACCESS_ROLE };
            return (UserDetails) User.builder()
                    .username(adminUsername)
                    .password(encoder.encode(adminPassword))
                    .authorities(authorities)
                    .build();
        }
        return repo.findByEmailOrUsername(username);
    }

    @Transactional
    public UserModel updateUser(UserModel model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.getName().equals("anonymousUser")
                || authentication.getAuthorities().contains(Authority.OP_ACCESS_ADMIN)
                || authentication.getName().equals(model.getEmail())) {
            try {
                validateUserData(model);
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
        UserModel model = repo.findUserById(user.getId());
        try {
            String path = ResourceUtils.getFile("classpath:static/img/profiles/").getAbsolutePath() + File.separator
                    + model.getProfilePicture();
            Files.delete(Paths.get(path));

            repo.deleteById(user.getId());
            refreshService.deleteTokenByUserId(user.getId());
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
        if (model.getUsername().equals(getAdminUsername())) {
            authenticateUser(model, null, null, response);
            refreshModel = refreshService.getRefreshByUserId(getAdminId());
        } else {
            refreshModel = refreshService.getRefreshByUserId(getUserIdByUsernameOrEmail(model.getUsername()));
        }

        if (refreshModel == null || !jwtUtils.isTokenExpired(refreshModel.getRefreshToken())) {
            try {
                if (!model.getUsername().equals(getAdminUsername())) {
                    authenticateUser(model, getUserIdByUsernameOrEmail(model.getUsername()), null, response);
                }
                return new ResponseEntity<>(repo.findByEmailOrUsername(model.getUsername()), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>("Already logged in!", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> signUpUser(UserModel model, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getName().equals("anonymousUser")
                || authentication.getAuthorities().contains(Authority.OP_ACCESS_ADMIN)
                || authentication.getName().equals(model.getEmail())) {
            try {
                String rawPass = model.getPassword();
                validateUserData(model);
                repo.save(model);
                JwtAuth jwtAuth = new JwtAuth();
                jwtAuth.setUsername(model.getEmail());
                jwtAuth.setPassword(model.getPassword());
                authenticateUser(jwtAuth, rawPass, response);
                return new ResponseEntity<>(repo.findByEmailOrUsername(model.getUsername()), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    private void authenticateUser(JwtAuth model, Long userId, String rawPass, HttpServletResponse response) {
        String username = model.getUsername();
        String password = model.getPassword();
        if (rawPass != null) {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(username, rawPass));
        } else {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        }
        RefreshModel rModel = new RefreshModel();
        String accessToken = jwtUtils.generateAccessToken(username);
        String refreshToken = jwtUtils.generateRefreshToken(username);
        rModel.setRefreshToken(refreshToken);
        rModel.setAccessToken(accessToken);
        if (model.getUsername().equals(getAdminUsername())) {
            rModel.setUserId(getAdminId());
            rModel.setId(refreshService.getIdByUserId(getAdminId()));
        } else {
            rModel.setId(refreshService.getIdByUserId(userId));
            rModel.setUserId(getUserIdByUsernameOrEmail(username));
        }
        refreshService.saveToken(rModel);
        response.addHeader("AccessToken", accessToken);
        response.addHeader("RefreshToken", refreshToken);
    }

    private void validateUserData(UserModel model) throws FileNotFoundException, IOException, Exception {
        model.setRoles(roleService.getRole("USER"));

        if (model.getUserName() == null || model.getUserName().trim().equals("")) {
            model.setUserName(model.getEmail().split("@")[0]);
        }

        UserModel preModel = repo.findUserById(model.getId());

        if (model.getId() != null && model.getFile() != null) {
            String path = ResourceUtils.getFile("classpath:static/img/profiles/").getAbsolutePath() + File.separator
                    + preModel.getProfilePicture();
            Files.delete(Paths.get(path));
        }

        if (preModel != null && preModel.getProfilePicture() != null) {
            model.setProfilePicture(preModel.getProfilePicture());
        }

        String fileName = saveFile(model.getFile());
        if (fileName != null) {
            model.setProfilePicture(fileName);
        }
        model.setPassword(encoder.encode(model.getPassword()));
    }

    private String saveFile(MultipartFile file) throws Exception {
        if (file != null) {
            String path = ResourceUtils.getFile("classpath:static/img/profiles/").getAbsolutePath();
            byte[] bytes = file.getBytes();
            String fileName = UUID.randomUUID() + "." + file.getContentType().split("/")[1];
            Files.write(Paths.get(path + File.separator + fileName), bytes);
            return fileName;
        }
        return null;
    }

    public Long getUserIdByUsernameOrEmail(String username) {
        return repo.findUserIdByUsername(username);
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public Long getAdminId() {
        return adminId;
    }
}
