package com.subhajit.job_portal_api.filter;

import com.subhajit.job_portal_api.repository.UserRepository;
import com.subhajit.job_portal_api.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private UserRepository userRepository;
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // see for authorization header: Bearer <token>
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // if not present continue with the route and return
        if(Objects.isNull(authHeader) || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        // extract the token from that
        final String authToken = authHeader.split(" ")[1].trim();

        // get the userDetails from token
        UserDetails userDetails = userRepository
                .findByUsername(jwtUtil.getUsernameFromToken(authToken))
                .orElse(null);

        // check if the token is valid
        if(!jwtUtil.validateToken(authToken, userDetails)){
            filterChain.doFilter(request, response);
            return;
        }

        // create authentication object from present credentials
        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails == null ?
                        List.of() : userDetails.getAuthorities()
        );

        // used for additional details about the request, ip address and sessionId
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        // the authentication details are added to the context , and is now available in whole application
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
