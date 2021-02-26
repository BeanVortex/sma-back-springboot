package ir.darkdeveloper.sma.Configs.Security.JWT;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtUtils {
    //                                      s    m    h    d     milli
    private static final int EXPIRE_TIME = (60 * 60 * 24 * 2) * 1000;

    @Value("${jwt.secretkey}")
    private String secret;

    public String generateToken(String username) {
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, secret)
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .compact();
    }


    public String getUsername(String token){
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

}

