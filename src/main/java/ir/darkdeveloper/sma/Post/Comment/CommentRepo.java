package ir.darkdeveloper.sma.Post.Comment;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepo extends PagingAndSortingRepository<CommentModel, Long> {
}
