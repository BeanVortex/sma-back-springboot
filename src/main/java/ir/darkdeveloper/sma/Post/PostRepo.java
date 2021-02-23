package ir.darkdeveloper.sma.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepo extends PagingAndSortingRepository<PostModel, Integer> {

    Page<PostModel> findByContentAndTitleContains(@Nullable String content,@Nullable String title, Pageable pageable);

    @NonNull
    Page<PostModel> findAll(@NonNull Pageable pageable);

    void deleteById (Long id);

    PostModel findById(Long id);
}
