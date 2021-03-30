package ir.darkdeveloper.sma.Users.Service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ir.darkdeveloper.sma.Users.Models.UserRoles;
import ir.darkdeveloper.sma.Users.Repo.UserRolesRepo;

@Service
public class UserRolesService {

    private final UserRolesRepo repo;

    @Autowired
    public UserRolesService(UserRolesRepo repo) {
        this.repo = repo;
    }

    @Transactional
    public ResponseEntity<?> saveRole(UserRoles role) {
        try {
            repo.save(role);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

	public List<UserRoles> getAllRoles() {
		return repo.findAll();
	}

    public List<UserRoles> getRole(String name){
        return repo.findByName(name);
    }

	public ResponseEntity<?> deleteRole(Long id) {
		try {
            repo.deleteById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
	}

    public Boolean exists(String name){
        if(repo.getUSER(name) != null){
            return true;
        }
        return false;
    }

}
