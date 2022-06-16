package ir.darkdeveloper.sma.config;

import ir.darkdeveloper.sma.model.Authority;
import ir.darkdeveloper.sma.model.UserRoles;
import ir.darkdeveloper.sma.service.UserRolesService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RoleConfig {

    private final UserRolesService service;

    private void createDefaultRole() {
        if (!service.exists("USER")) {
            var authorities = List.of(Authority.OP_EDIT_USER, Authority.OP_ACCESS_USER, Authority.OP_DELETE_USER);
            service.saveRole(new UserRoles(1L, "USER", authorities));
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        createDefaultRole();
    }
}
