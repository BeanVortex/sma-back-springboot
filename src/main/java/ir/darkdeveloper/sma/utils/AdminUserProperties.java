package ir.darkdeveloper.sma.utils;

import ir.darkdeveloper.sma.model.Authority;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConfigurationProperties(prefix = "user.admin")
public record AdminUserProperties(Long id, String username, String password,
                                  List<Authority> authorities) {

}