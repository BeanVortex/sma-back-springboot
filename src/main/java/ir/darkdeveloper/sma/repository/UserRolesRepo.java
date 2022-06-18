package ir.darkdeveloper.sma.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ir.darkdeveloper.sma.model.UserRoles;



@Repository
public interface UserRolesRepo extends JpaRepository<UserRoles, Long>{

    
    List<UserRoles> findByName(String name);

    @Query("SELECT model FROM UserRoles model WHERE model.name = :name")
    UserRoles getUSER(@Param("name") String name);

}
