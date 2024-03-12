package ir.darkdeveloper.sma.utils;

import ir.darkdeveloper.sma.exceptions.NoContentException;
import ir.darkdeveloper.sma.model.RefreshModel;
import ir.darkdeveloper.sma.service.RefreshService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
public class JwtFilter extends OncePerRequestFilter {

    @Lazy
    private final UserUtils userUtils;
    private final RefreshService refreshService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        var refreshToken = Optional.ofNullable(request.getHeader("refresh_token"));
        var accessToken = Optional.ofNullable(request.getHeader("access_token"));


        if (refreshToken.isPresent() && accessToken.isPresent()
                && !JwtUtils.isTokenExpired(refreshToken.get())) {

            var username = JwtUtils.getUsername(refreshToken.get());
            var userId = ((Integer) JwtUtils.getAllClaimsFromToken(refreshToken.get())
                    .get("user_id")).longValue();
            authenticateUser(username);
            setUpHeader(response, accessToken.get(), username, userId);
        }

        filterChain.doFilter(request, response);
    }


    private void authenticateUser(String username) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (username != null && auth == null) {
            //db query
            var userDetails = userUtils.loadUserByUsername(username)
                    .orElseThrow(() -> new NoContentException("User does not exist"));
            var upToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                    userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(upToken);

        }
    }

    private void setUpHeader(HttpServletResponse response, String accessToken, String username,
                             Long userId) {


        // if this didn't execute, it means the access token is still valid
        if (JwtUtils.isTokenExpired(accessToken)) {
            //db query
            var storedRefreshModel = refreshService.getRefreshByUserId(userId);
            var storedAccessToken = storedRefreshModel.getAccessToken();
            var storedRefreshToken = storedRefreshModel.getRefreshToken();
            if (accessToken.equals(storedAccessToken)) {
                var newAccessToken = JwtUtils.generateAccessToken(username);
                var refreshModel = new RefreshModel();
                refreshModel.setAccessToken(newAccessToken);
                refreshModel.setRefreshToken(storedRefreshToken);
                refreshModel.setUserId(userId);
                refreshModel.setId(storedRefreshModel.getId());
                // db query
                refreshService.saveToken(refreshModel);
                response.addHeader("access_token", newAccessToken);
            } else
                //if stored token is not equal with user send token, it will return 403
                SecurityContextHolder.getContext().setAuthentication(null);

        }
    }


}
