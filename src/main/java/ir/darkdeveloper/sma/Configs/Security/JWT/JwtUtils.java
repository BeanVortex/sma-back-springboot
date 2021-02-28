package ir.darkdeveloper.sma.Configs.Security.JWT;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtUtils {

    @Value("${jwt.secretkey}")
    private String secret;

    private final PasswordEncoder encoder;

    @Autowired
    public JwtUtils(PasswordEncoder encoder) {
        this.encoder = encoder;
        secret = this.encoder.encode(secret);
    }

    //refresh token is used to generate access token
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, secret)
                .setSubject(username)
                .setExpiration(new Date(LocalDateTime.now().plusWeeks(3).getSecond() * 1000))
                .compact();
    }

    public String generateAccessToken(String username) {
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, secret)
                .setSubject(username)
                .setExpiration(new Date(LocalDateTime.now().plusSeconds(60).getSecond() * 1000))
                .compact();
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDate(token);
        return expiration.before(new Date());
    }

    private Date getExpirationDate(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getExpiration();
    }

}
