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

import ir.darkdeveloper.sma.Configs.Security.JWT.Crud.RefreshService;
import ir.darkdeveloper.sma.Users.Service.UserService;

@Service
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserService userService;
    private RefreshService refreshService;

    @Autowired
    public JwtFilter(JwtUtils jwtUtils, UserService userService, RefreshService refreshService) {
        this.jwtUtils = jwtUtils;
        this.refreshService = refreshService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        //String token = request.getHeader("Authorization");

        String refreshToken = request.getHeader("RefreshToken");
        String accessToken = request.getHeader("AccessToken");
        Long userId = Long.valueOf(request.getHeader("UserId"));

        String storedAccessToken = refreshService.getRefreshByUserId(userId).getAccessToken();
        String storedRefreshToken = refreshService.getRefreshByUserId(userId).getRefreshToken();

        if (refreshToken != null && accessToken != null) {
            if (accessToken.equals(storedAccessToken) && refreshToken.equals(storedRefreshToken)) {

                String username = jwtUtils.getUsername(refreshToken);
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();

                if (username != null && auth == null || jwtUtils.isTokenExpired(accessToken)) {
                    UserDetails userDetails = userService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(userDetails,
                            null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(upToken);
                    String newAccessToken = jwtUtils.generateAccessToken(username);
                    refreshService.updateTokenByUserId(userId, newAccessToken);
                    response.addHeader("AccessToken", newAccessToken);
                    response.addHeader("RefreshToken", refreshToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
