package com.example.bezbednost.bezbednost.config;

import com.example.bezbednost.bezbednost.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Component
public class TokenUtils {
    private static final String AUDIENCE_WEB = "web";
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;

    @Value("spring-security-example")
    private String appName;

    @Value("somesecret")
    private String secret;

    @Value("3600000")
    private int expiresIn;

    @Value("Authorization")
    private String authHeader;

    public String generateToken(String username, List<String> roles, List<String> authorities) {
        return Jwts.builder()
                .setIssuer(appName)
                .setSubject(username)
                .claim("roles", roles)
                .claim("authorities", authorities)
                .setAudience(generateAudience())
                .setIssuedAt(new Date())
                .setExpiration(generateExpirationDate())
                .signWith(signatureAlgorithm, secret).compact();
    }

    public String getToken(HttpServletRequest request) {
        String authHeader = getAuthHeaderFromHeader(request);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    public String getUsernameFromToken(String token) {
        String username = "";
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            if (claims != null) {
                username = claims.getSubject();
                return username;
            }
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            username = null;
        }

        return username;
    }

    public Date getIssuedDateFromToken(String token) {
        Date issuedAt = new Date();

        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            if (claims != null) {
                issuedAt = claims.getIssuedAt();
                return issuedAt;
            }
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            issuedAt = null;
        }

        return issuedAt;
    }

    public String getAudienceFromToken(String token) {
        String audience = "";

        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            if (claims != null) {
                audience = claims.getAudience();
                return audience;
            }
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            audience = null;
        }

        return audience;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expirationDate = new Date();

        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            if (claims != null) {
                expirationDate = claims.getExpiration();
                return expirationDate;
            }
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            expirationDate = null;
        }

        return expirationDate;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        User user = (User) userDetails;
        final String username = getUsernameFromToken(token);
        final Date created = getIssuedDateFromToken(token);
        return (username != null && username.equals(userDetails.getUsername()));
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

    private Claims getAllClaimsFromToken(String token) {
        Claims claims;

        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    private String getAuthHeaderFromHeader(HttpServletRequest request) {
        return request.getHeader(authHeader);
    }

    private String generateAudience() {
        return AUDIENCE_WEB;
    }

    private Date generateExpirationDate() {
        return new Date(new Date().getTime() + expiresIn);
    }
}
