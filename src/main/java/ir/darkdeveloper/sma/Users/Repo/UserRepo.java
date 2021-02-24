package ir.darkdeveloper.sma.Users.Repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ir.darkdeveloper.sma.Users.Models.UserModel;

@Repository
public interface UserRepo extends PagingAndSortingRepository<UserModel, Long> {

    @Query("SELECT model FROM UserModel model WHERE model.email = :username OR model.userName = :username")
    public UserModel findByEmailOrUsername(@Param("username") String username);

    public UserModel findUserById(Long id);

    @Query("SELECT model FROM UserModel model")
    public Page<UserModel> findAll(Pageable pageable);

    // @Query("SELECT * FROM UserModel model WHERE model.username = :username")
    // public Page<UserModel> findByUsername(@Param("username") String username, Pageable pageable);

}
