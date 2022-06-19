package ir.darkdeveloper.sma.config;

import ir.darkdeveloper.sma.model.Authority;
import ir.darkdeveloper.sma.model.UserRoles;
import ir.darkdeveloper.sma.service.UserRolesService;
import ir.darkdeveloper.sma.utils.AdminUserProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(AdminUserProperties.class)
@Slf4j
public class StartupConfig {

    private final UserRolesService service;

    public static final String DATE_FORMAT = "EE MMM dd yyyy HH:mm:ss";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    @Bean
    public String dateFormat() {
        return DATE_FORMAT;
    }

    @Bean
    public DateTimeFormatter dateFormatter() {
        return DATE_FORMATTER;
    }

    private void createDefaultRole() {
        if (!service.exists("USER")) {
            var authorities = List.of(
                    Authority.OP_EDIT_USER,
                    Authority.OP_ACCESS_USER,
                    Authority.OP_DELETE_USER,
                    Authority.OP_ADD_POST,
                    Authority.OP_EDIT_POST,
                    Authority.OP_DELETE_POST,
                    Authority.OP_ADD_COMMENT,
                    Authority.OP_EDIT_COMMENT,
                    Authority.OP_DELETE_COMMENT
            );
            service.saveRole(new UserRoles(1L, "USER", authorities));
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        createDefaultRole();
        log.info("Inserted the Default Role");
    }
}
