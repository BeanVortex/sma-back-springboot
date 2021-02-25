package ir.darkdeveloper.sma.Users.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import ir.darkdeveloper.sma.Users.Models.Authority;
import ir.darkdeveloper.sma.Users.Models.UserModel;
import ir.darkdeveloper.sma.Users.Repo.UserRepo;

@Service("userService")
public class UserService implements UserDetailsService {

    @Value("${spring.security.user.name}")
    private String adminUsername;

    @Value("${spring.security.user.password}")
    private String adminPassword;

    private final PasswordEncoder encoder;
    private final UserRepo repo;
    private final UserRolesService roleService;

    @Autowired
    public UserService(@Lazy PasswordEncoder encoder, UserRepo repo, UserRolesService roleService) {
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
            return User.builder().username(adminUsername).password(encoder.encode(adminPassword))
                    .authorities(authorities).build();
        }
        return repo.findByEmailOrUsername(username);
    }

    @Transactional
    public UserModel saveUser(UserModel model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getName().equals("anonymousUser") || authentication.getAuthorities().contains(Authority.OP_ACCESS_ADMIN)
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

    @Transactional
    @PreAuthorize("authentication.name == this.getAdminUsername() || #user.getEmail() == authentication.name")
    public ResponseEntity<?> deleteUser(UserModel user) {
        UserModel model = repo.findUserById(user.getId());
        try {
            String path = ResourceUtils.getFile("classpath:static/img/profiles/").getAbsolutePath() + File.separator
                    + model.getProfilePicture();
            Files.delete(Paths.get(path));
            repo.deleteById(user.getId());
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

    // public Page<UserModel> searchUser(String username, Pageable pageable){
    // return repo.findByUsername(username, pageable);
    // }

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

    public String getAdminUsername() {
        return adminUsername;
    }
}
