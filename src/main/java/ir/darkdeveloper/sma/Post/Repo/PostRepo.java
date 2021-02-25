package ir.darkdeveloper.sma.Post.Repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import ir.darkdeveloper.sma.Post.Models.PostModel;

@Repository
public interface PostRepo extends PagingAndSortingRepository<PostModel, Integer> {

    Page<PostModel> findByContentAndTitleContains( String content, String title, Pageable pageable);

    Page<PostModel> findAll(Pageable pageable);

    void deleteById (Long id);

    public PostModel findById(Long id);
}
