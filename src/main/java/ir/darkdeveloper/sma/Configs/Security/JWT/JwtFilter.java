package ir.darkdeveloper.sma.Configs.Security.JWT;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import ir.darkdeveloper.sma.Users.Service.UserService;

@Service
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserService userService;

    @Autowired
    public JwtFilter(JwtUtils jwtUtils, UserService userService) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token != null) {
            String username = jwtUtils.getUsername(token);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (username != null && auth == null) {
                UserDetails userDetails = userService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(upToken);
            }

        }
        filterChain.doFilter(request, response);
    }
}
