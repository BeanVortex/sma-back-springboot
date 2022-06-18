package ir.darkdeveloper.sma.utils;


import ir.darkdeveloper.sma.exceptions.PasswordException;
import ir.darkdeveloper.sma.model.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PasswordUtils {

    private final PasswordEncoder encoder;

    /**
     * updates preUser Password
     */
    public void updatePasswordUsingPrevious(Optional<UserModel> user, UserModel foundUser) {
        //update pass
        var passExists = user.map(UserModel::getPassword).isPresent();
        var rPassExists = user.map(UserModel::getPasswordRepeat).isPresent();
        if (passExists && rPassExists) {
            user.map(UserModel::getPrevPassword).ifPresentOrElse(
                    prePass -> {
                        if (encoder.matches(prePass, foundUser.getPassword()))
                            foundUser.setPassword(encoder.encode(user.get().getPassword()));
                        else
                            throw new PasswordException("Previous password is wrong");
                    },
                    () -> {
                        throw new PasswordException("Enter previous password to change");
                    });
        }
        //else keep foundUser pass
    }

    public void passEqualityChecker(Optional<UserModel> user) {
        if (user.isPresent() && !user.get().getPassword().equals(user.get().getPasswordRepeat()))
            throw new PasswordException("Passwords do not match");
    }
}