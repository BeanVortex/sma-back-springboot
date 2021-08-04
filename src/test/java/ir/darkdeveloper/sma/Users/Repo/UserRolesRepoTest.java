package ir.darkdeveloper.sma.Users.Repo;

import ir.darkdeveloper.sma.Users.Models.UserRoles;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class UserRolesRepoTest {

    private final UserRolesRepo repo;

    @Autowired
    UserRolesRepoTest(UserRolesRepo repo) {
        this.repo = repo;
    }

    @Test
    void getUSER() {
        UserRoles role = repo.getUSER("USER");
        assertThat(role.getName()).isEqualTo("USER");
    }
}