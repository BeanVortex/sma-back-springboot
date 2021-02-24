package ir.darkdeveloper.sma.Post.Repo;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import ir.darkdeveloper.sma.Post.Models.CommentModel;

@Repository
public interface CommentRepo extends PagingAndSortingRepository<CommentModel, Long> {
}
