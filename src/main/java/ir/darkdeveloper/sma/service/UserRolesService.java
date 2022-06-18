package ir.darkdeveloper.sma.service;

import ir.darkdeveloper.sma.model.UserRoles;
import ir.darkdeveloper.sma.repository.UserRolesRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static ir.darkdeveloper.sma.utils.Generics.exceptionHandlers;

@Service
@RequiredArgsConstructor
public class UserRolesService {

    private final UserRolesRepo repo;

    @Transactional
    public UserRoles saveRole(UserRoles role) {
        return exceptionHandlers(() -> repo.save(role));
    }


    public List<UserRoles> getAllRoles() {
        return exceptionHandlers(repo::findAll);
    }

    public UserRoles getRoles(String name) {
        return exceptionHandlers(() -> repo.findByName(name));
    }

    public String deleteRole(Long id) {
        return exceptionHandlers(() -> {
            repo.deleteById(id);
            return "deleted";
        });
    }

    public Boolean exists(String name) {
        return repo.getUSER(name) != null;
    }

}