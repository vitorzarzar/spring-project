package challenge.springproject.business;

import challenge.springproject.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class TokenAuthenticationService {

    private static Key key;

    //30 minutos
    private static final long expirationTime = 1800000;

    public TokenAuthenticationService() {
        key =  Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public String generateAuthentication(User user) {
        return Jwts.builder()
                .setIssuer("springproject")
                .setAudience("springproject")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setSubject(user.getId().toString())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key).compact();
    }

    public Claims decodeAuthentication(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJwt(token)
                .getBody();
    }
}