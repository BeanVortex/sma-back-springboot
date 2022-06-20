package ir.darkdeveloper.sma.utils;

import ir.darkdeveloper.sma.dto.LoginDto;
import ir.darkdeveloper.sma.exceptions.BadRequestException;
import ir.darkdeveloper.sma.exceptions.ForbiddenException;
import ir.darkdeveloper.sma.exceptions.NoContentException;
import ir.darkdeveloper.sma.exceptions.PasswordException;
import ir.darkdeveloper.sma.model.RefreshModel;
import ir.darkdeveloper.sma.model.UserModel;
import ir.darkdeveloper.sma.repository.UserRepo;
import ir.darkdeveloper.sma.service.RefreshService;
import ir.darkdeveloper.sma.service.UserRolesService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

import static ir.darkdeveloper.sma.config.StartupConfig.DATE_FORMATTER;

@Component
@RequiredArgsConstructor
public class UserUtils {


    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;
    private final PasswordUtils passwordUtils;
    private final RefreshService refreshService;
    private final UserRepo repo;
    private final PasswordEncoder encoder;
    private final IOUtils ioUtils;
    private final UserRolesService rolesService;
    private final AdminUserProperties adminUser;


    public UserModel authenticateUser(Optional<LoginDto> loginDto, HttpServletResponse response) {
        var credentials = loginDto.orElseThrow(() -> new BadRequestException("Credentials can't be empty"));
        var username = credentials.username();
        var password = credentials.password();

        var user = new UserModel();

        var rModel = new RefreshModel();
        if (credentials.username().equals(adminUser.username())) {
            rModel.setUserId(adminUser.id());
            rModel.setId(refreshService.getIdByUserId(adminUser.id()));
            user.setEnabled(true);
        } else {
            user = repo.findByEmailOrUsername(username)
                    .orElseThrow(() -> new NoContentException("User does not exist"));
            rModel.setId(refreshService.getIdByUserId(user.getId()));
            rModel.setUserId(user.getId());
        }

        var auth = new UsernamePasswordAuthenticationToken(username, password);


        try {
            authManager.authenticate(auth);
        } catch (Exception e) {
            throw new BadRequestException("Bad Credentials");
        }

        var accessToken = jwtUtils.generateAccessToken(username);
        var refreshToken = jwtUtils.generateRefreshToken(username, rModel.getUserId());

        rModel.setAccessToken(accessToken);
        rModel.setRefreshToken(refreshToken);
        refreshService.saveToken(rModel);

        setupHeader(response, accessToken, refreshToken);
        return user;
    }

    private void validateUserData(Optional<UserModel> model) {

        model.map(UserModel::getId).ifPresent(id -> {
            throw new ForbiddenException("You are not allowed to sign up! :|");
        });

        model.map(UserModel::getEmail)
                .ifPresent(email -> {
                    var username = model.map(UserModel::getUserName);
                    if (username.isEmpty() || username.get().isBlank() || username.get().equals(adminUser.username()))
                        model.get().setUserName(email.split("@")[0]);
                });
        passwordUtils.passEqualityChecker(model);

    }

    public Long getUserIdByUsernameOrEmail(String username) {
        return repo.findUserIdByUsername(username);
    }



    public void setupHeader(HttpServletResponse response, String accessToken, String refreshToken) {
        var date = jwtUtils.getExpirationDate(refreshToken);
        var refreshDate = DATE_FORMATTER.format(date);
        response.addHeader("refresh_token", refreshToken);
        response.addHeader("access_token", accessToken);
        response.addHeader("refresh_expiration", refreshDate);
    }

    public Optional<? extends UserDetails> loadUserByUsername(String username) {
        if (username.equals(adminUser.username())) {
            var authorities = adminUser.authorities();
            return Optional.of(
                    User.builder().username(adminUser.username())
                            .password(encoder.encode(adminUser.password())).authorities(authorities).build()
            );
        }
        return repo.findByEmailOrUsername(username);
    }

    public UserModel signup(Optional<UserModel> model, HttpServletResponse res) {

        var user = model.orElseThrow(() -> new BadRequestException("Body can't be empty"));
        var rawPass = model.map(UserModel::getPassword)
                .orElseThrow(() -> new PasswordException("Password is required!"));
        validateUserData(model);
        user.setRoles(List.of(rolesService.getRoles("USER")));
        ioUtils.saveUserImages(user);
        user.setPassword(encoder.encode(user.getPassword()));
        repo.save(user);

        var loginDto = Optional.of(new LoginDto(user.getEmail(), rawPass));

        return authenticateUser(loginDto, res);

    }

    /**
     * @param userId if it is null, then it compares id in jwt token and id mapped to this token in database
     */
    public void checkUserIsSameUserForRequest(Long userId, HttpServletRequest req, String operation) {
        var token = req.getHeader("refresh_token");
        if (!jwtUtils.isTokenExpired(token)) {
            var id = jwtUtils.getUserId(token);
            if (userId != null && !id.equals(userId))
                throw new ForbiddenException("You don't have permission to " + operation);
            else {
                // in case when attacker tried to change the userId in refreshToken
                // db query
                var fetchedId = refreshService.getUserIdByRefreshToken(token)
                        .orElseThrow(() -> new ForbiddenException("You are logged out. Try logging in again"));
                if (!fetchedId.equals(id))
                    throw new ForbiddenException("You don't have permission to " + operation);
            }
        } else
            throw new ForbiddenException("You are logged out. Try logging in again");
    }
}
