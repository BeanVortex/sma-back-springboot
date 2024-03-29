package ir.darkdeveloper.sma.service;

import ir.darkdeveloper.sma.dto.LoginDto;
import ir.darkdeveloper.sma.exceptions.BadRequestException;
import ir.darkdeveloper.sma.exceptions.NoContentException;
import ir.darkdeveloper.sma.model.UserModel;
import ir.darkdeveloper.sma.repository.UserRepo;
import ir.darkdeveloper.sma.utils.IOUtils;
import ir.darkdeveloper.sma.utils.PasswordUtils;
import ir.darkdeveloper.sma.utils.UserUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static ir.darkdeveloper.sma.utils.ExceptionUtils.exceptionHandlers;

@Service("userService")
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
public class UserService implements UserDetailsService {

    private final UserRepo repo;
    @Lazy
    private final UserUtils userUtils;
    private final IOUtils ioUtils;
    private final RefreshService refreshService;
    private final PasswordUtils passwordUtils;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userUtils.loadUserByUsername(username)
                .orElseThrow(() -> new NoContentException("User wasn't found"));
    }

    @Transactional
    public UserModel updateUser(Optional<UserModel> model, HttpServletRequest req) {
        return exceptionHandlers(() -> {
            var user = model.orElseThrow(() -> new BadRequestException("User can't be null"));
            var id = model.map(UserModel::getId)
                    .orElseThrow(() -> new BadRequestException("body or id of user can't be null"));
            userUtils.checkUserIsSameUserForRequest(model.get().getId(), req, "update");
            var foundUser = repo.findById(id).orElseThrow(() -> new NoContentException("User not found"));

            if (user.getProfileFile() != null)
                ioUtils.saveFile(user.getProfileFile(), IOUtils.USER_IMAGE_PATH).ifPresent(foundUser::setProfilePicture);

            passwordUtils.updatePasswordUsingPrevious(Optional.of(user), foundUser);
            foundUser.update(user);
            return repo.save(foundUser);
        });
    }

    @Transactional
    public String deleteUser(Long id, HttpServletRequest req) {
        return exceptionHandlers(() -> {
            var user = repo.findUserById(id)
                    .orElseThrow(() -> new NoContentException("User does not exist"));
            userUtils.checkUserIsSameUserForRequest(id, req, "delete");

            ioUtils.deleteUserImages(user);
            user.getPosts().forEach(ioUtils::deletePostImagesOfUser);

            refreshService.deleteTokenByUserId(user.getId());
            repo.deleteById(user.getId());
            return "deleted";
        });
    }

    public Page<UserModel> findAll(Pageable pageable) {
        return exceptionHandlers(() -> repo.findAll(pageable));
    }

    public UserModel loginUser(Optional<LoginDto> loginDto, HttpServletResponse res) {
        return exceptionHandlers(() -> userUtils.authenticateUser(loginDto, res));
    }

    @Transactional
    public UserModel signUpUser(Optional<UserModel> model, HttpServletResponse res) {
        return exceptionHandlers(() -> userUtils.signup(model, res));
    }

    public UserModel getUserInfo(Long id) {
        return repo.findUserById(id).orElseThrow(() -> new NoContentException("User wasn't found"));
    }


}
