package ir.darkdeveloper.sma.utils;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import ir.darkdeveloper.sma.model.RefreshModel;
import ir.darkdeveloper.sma.service.RefreshService;

@Service
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserUtils userUtils;
    private final RefreshService refreshService;
    private final AdminUserProperties adminUserProperties;

    public JwtFilter(@Lazy UserUtils userUtils, JwtUtils jwtUtils, RefreshService refreshService,
                     AdminUserProperties adminUserProperties) {

        this.jwtUtils = jwtUtils;
        this.userUtils = userUtils;
        this.refreshService = refreshService;
        this.adminUserProperties = adminUserProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        //String token = request.getHeader("Authorization");

        String refreshToken = request.getHeader("refresh_token");
        String accessToken = request.getHeader("access_token");

        if (refreshToken != null && accessToken != null) {
            String username = jwtUtils.getUsername(refreshToken);

            Long userId = username.equals(adminUserProperties.username()) ? adminUserProperties.id()
                    : userUtils.getUserIdByUsernameOrEmail(username);

            String storedAccessToken = refreshService.getRefreshByUserId(userId).getAccessToken();
            String storedRefreshToken = refreshService.getRefreshByUserId(userId).getRefreshToken();
            if (storedAccessToken != null && storedRefreshToken != null && accessToken.equals(storedAccessToken)
                    && refreshToken.equals(storedRefreshToken) && !jwtUtils.isTokenExpired(storedRefreshToken)) {

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();

                if (username != null && auth == null) {
                    var userDetails = userUtils.loadUserByUsername(username).orElseThrow();
                    UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(userDetails,
                            null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(upToken);
                    String newAccessToken = jwtUtils.generateAccessToken(username);
                    RefreshModel refreshModel = new RefreshModel();
                    refreshModel.setAccessToken(newAccessToken);
                    refreshModel.setRefreshToken(storedRefreshToken);
                    refreshModel.setUserId(userId);
                    if (username.equals(adminUserProperties.username())) {
                        refreshModel.setId(refreshService.getIdByUserId(adminUserProperties.id()));
                        response.addHeader("user_id", "" + refreshModel.getId());
                    } else {
                        refreshModel
                                .setId(refreshService.getIdByUserId(userUtils.getUserIdByUsernameOrEmail(username)));
                    }
                    refreshService.saveToken(refreshModel);
                    response.addHeader("access_token", newAccessToken);
                    response.addHeader("refresh_token", refreshToken);
                }

            }
        }

        filterChain.doFilter(request, response);
    }

    /*     public boolean userAdminEndpointCheck(HttpServletRequest request, UserDetails model){
        String endPoint = request.getRequestURI();
        switch(endPoint){
            case "/api/user/role/":
            return model.getAuthorities().contains(Authority.OP_ACCESS_ROLE);
            case "/api/user/update/":
            return model.getAuthorities().contains(Authority.OP_ACCESS_ROLE);
        }
    } */

}
