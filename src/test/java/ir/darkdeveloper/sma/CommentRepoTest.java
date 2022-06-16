package ir.darkdeveloper.sma;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import ir.darkdeveloper.sma.model.CommentModel;
import ir.darkdeveloper.sma.repository.CommentRepo;

@DataJpaTest
public class CommentRepoTest {

    @Autowired
    private  CommentRepo repo;

    @Test
    void findById(){
        Integer id = 1;
        CommentModel expCommentModel = repo.findById(id);
        assertNull(expCommentModel, "is null");

    }
}
