package ir.darkdeveloper.sma.Configs.Security.JWT.Crud;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshRepo extends JpaRepository<RefreshModel, Long> {

    @Query("DELETE FROM RefreshModel model WHERE user_id=:id")
    void deleteTokenByUserId(@Param("id") Long id);

    @Query("SELECT model FROM RefreshModel model WHERE model.user_id=:id")
    RefreshModel getRefreshByUserId(@Param("id") Long id);

    @Query("UPDATE RefreshModel model SET model.access_token = :token WHERE model.user_id=:id")
    RefreshModel updateTokenByUserId(@Param("id") Long userId, @Param("token") String accessToken);

}
