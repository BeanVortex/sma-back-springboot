package ir.darkdeveloper.sma.Users.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ir.darkdeveloper.sma.Users.UserModels.UserRoles;


@Repository
public interface UserRolesRepository extends JpaRepository<UserRoles, Long>{
    
    // @Query("SELECT role FROM UserRoles role WHERE role.name = :name")
    // public UserRoles findByName(String name);

    public List<UserRoles> findByName(String name);
}
