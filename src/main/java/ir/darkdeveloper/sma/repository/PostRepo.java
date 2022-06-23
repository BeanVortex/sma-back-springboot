package ir.darkdeveloper.sma.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ir.darkdeveloper.sma.model.PostModel;

import java.util.Optional;

@Repository
public interface PostRepo extends PagingAndSortingRepository<PostModel, Long> {

    @Query("SELECT m from PostModel m " +
            "WHERE UPPER(m.title) LIKE UPPER(CONCAT( '%', :title,'%')) " +
            "OR UPPER(m.content) LIKE UPPER(CONCAT( '%', :content,'%')) ")
    Page<PostModel> findByContentAndTitleContains(String content, String title, Pageable pageable);

    Optional<PostModel> findPostById(Long id);

    @Query("SELECT model from PostModel model WHERE model.user.id = :id")
    Page<PostModel> getOneUserPosts(@Param("id") Long userId, Pageable pageable);
}
