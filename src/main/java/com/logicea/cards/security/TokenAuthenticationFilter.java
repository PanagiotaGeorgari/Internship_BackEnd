package com.logicea.cards.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public TokenAuthenticationFilter(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        String path = request.getServletPath();
        if (path.equals("/user-info/register") || path.equals("/user-info/token")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (token != null && !token.isEmpty()) {
            try {

                String decoded = new String(Base64.getDecoder().decode(token));//decode the token
                String[] tokenDecoded = decoded.split(":");


                if (tokenDecoded.length == 2) { // we have 2 parts email:password
                    String email = tokenDecoded[0];
                    String rawPassword = tokenDecoded[1];

                    UserDetails userDetails = userDetailsService.loadUserByUsername(email); //load user from the base

                    if (passwordEncoder.matches(rawPassword, userDetails.getPassword())) { // check if password matches with the encoded password in the base
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(auth); // updates spring that user is authenticated
                    } else {
                        sendUnauthorized(response, "Invalid username or password");
                        return;
                    }
                }
            } catch (Exception e) {
                sendUnauthorized(response, "Invalid tokens!");
                return;
            }
        }


        filterChain.doFilter(request, response);
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + message + "\"}");
    }
}
