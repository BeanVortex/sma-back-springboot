package ir.darkdeveloper.sma.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ir.darkdeveloper.sma.model.UserModel;

import java.util.Optional;

@Repository
public interface UserRepo extends PagingAndSortingRepository<UserModel, Long>, ListCrudRepository<UserModel, Long> {

    @Query("SELECT model FROM UserModel model WHERE model.email = :username OR model.userName = :username")
    Optional<UserModel> findByEmailOrUsername(@Param("username") String username);

    Optional<UserModel> findUserById(Long id);

    Page<UserModel> findAll(Pageable pageable);

    @Query("SELECT model.id FROM UserModel model WHERE model.email = :username OR model.userName = :username")
    Long findUserIdByUsername(@Param("username") String username);
}
