package com.example.bezbednost.bezbednost.config;

import com.example.bezbednost.bezbednost.service.CustomUserService;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TokenAuthenticationFilter extends OncePerRequestFilter {
    protected final Log logger = LogFactory.getLog(getClass());
    private final TokenUtils tokenUtils;
    private final CustomUserService userService;

    public TokenAuthenticationFilter(TokenUtils tokenHelper, CustomUserService userService) {
        this.tokenUtils = tokenHelper;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String username;

        String authToken = tokenUtils.getToken(request);
        try {
            if (authToken != null) {
                username = tokenUtils.getUsernameFromToken(authToken);
                if (username != null) {
                    UserDetails userDetails = userService.loadUserByUsername(username);
                    if (this.tokenUtils.validateToken(authToken, userDetails)) {
                        TokenBasedAuthentication authentication = new TokenBasedAuthentication(userDetails);
                        authentication.setToken(authToken);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        } catch (ExpiredJwtException ex) {
            logger.debug("Token expired!");
        }

        filterChain.doFilter(request, response);
    }
}
