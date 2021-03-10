package ir.darkdeveloper.sma.Post.Repo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ir.darkdeveloper.sma.Post.Models.CommentModel;

@Repository
public interface CommentRepo extends PagingAndSortingRepository<CommentModel, Long> {
    @Query("SELECT model FROM CommentModel model WHERE model.id = :id")
    public CommentModel findById(@Param("id") Integer id);
}
