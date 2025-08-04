package ir.maktabsharif.online_exam.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-ms}")
    private Long validity;

    private Algorithm algorithm() {
        return Algorithm.HMAC256(secretKey);
    }

    public String generateToken(UserDetails userDetails){
        Date date = new Date();
        Date expirationDate = new Date(date.getTime() + validity);
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuedAt(date)
                .withExpiresAt(expirationDate)
                .withClaim("role" , userDetails.getAuthorities().iterator().next().getAuthority())
                .sign(algorithm());
    }

    public String getUsernameFromToken(String token) {
        return validateToken(token).getSubject();
    }

    public String getRoleFromToken(String token){
        return validateToken(token).getClaim("role").asString();
    }

    private DecodedJWT validateToken(String token) {
        return JWT.require(algorithm()).build().verify(token);
    }

}
