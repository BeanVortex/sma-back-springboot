package ir.darkdeveloper.sma.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ir.darkdeveloper.sma.model.CommentModel;

@Repository
public interface CommentRepo extends PagingAndSortingRepository<CommentModel, Long>, ListCrudRepository<CommentModel, Long> {
    @Query("SELECT model FROM CommentModel model WHERE model.id = :id")
    CommentModel findById(@Param("id") Integer id);

    @Query("SELECT model FROM CommentModel model WHERE model.post.id = :postId")
    Page<CommentModel> findCommentByPostId(@Param("postId") Long id, Pageable pageable);
}
