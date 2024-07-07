package com.sb.shippingbackend.service;

import com.sb.shippingbackend.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Component
public class JWTUtils {
    private final TokenRepository tokenRepository;
    private SecretKey key;
    private static final long EXPIRATION_TIME  = 86400000;

    public JWTUtils(TokenRepository tokenRepository){
        String secretString = "abcdefkasdlnascon1128u39123n1kj23nanc1k23123mn123ku";
        byte[] keyBytes = Base64.getDecoder().decode(secretString.getBytes(StandardCharsets.UTF_8));
        this.key = new SecretKeySpec(keyBytes,"HmacSHA256");
        this.tokenRepository = tokenRepository;
    }
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(HashMap<String, Object> claims, UserDetails userDetails)
    {
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload());
    }

    public Boolean isTokenValid(String token, UserDetails userDetails) {
        final String extractedUsername = extractUsername(token);
        boolean isValidToken = tokenRepository.findByToken(token)
                .map(t -> !t.isLoggedOut()).orElse(false);
        return (extractedUsername.equals(userDetails.getUsername()) && !isTokenExpired(token)) && isValidToken ;
    }

    private Boolean isTokenExpired(String token) {
        return extractClaim(token,Claims::getExpiration).before(new Date());
    }

    public Boolean checkTokenExpired(String token)
    {
        return extractClaim(token,Claims::getExpiration).before(new Date());
    }




}
