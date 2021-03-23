package ir.darkdeveloper.sma.Post.Repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ir.darkdeveloper.sma.Post.Models.PostModel;

@Repository
public interface PostRepo extends PagingAndSortingRepository<PostModel, Long> {

    Page<PostModel> findByContentAndTitleContains( String content, String title, Pageable pageable);


    void deleteById (Long id);

    @Query("SELECT model FROM PostModel model WHERE model.id = :id")
    public PostModel findPostById(@Param("id") Long id);
}
