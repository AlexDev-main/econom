package com.econocom.authentication.infrastructure.security.filter;

import com.econocom.authentication.domain.model.User;
import com.econocom.authentication.domain.port.out.JwtProviderPort;
import com.econocom.authentication.domain.port.out.UserRepositoryPort;
import com.econocom.authentication.domain.model.JwtPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

    private final JwtProviderPort jwtProvider;
    private final UserRepositoryPort userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null) {

            try {

                JwtPayload payload = jwtProvider.parseToken(token);

                authenticateUser(payload.getEmail(), request);

            } catch (Exception ignored) {
                // El AuthenticationEntryPoint responderá con 401
            }

        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {

        String header = request.getHeader(AUTHORIZATION);

        if (header == null || !header.startsWith(BEARER)) {
            return null;
        }

        return header.substring(BEARER.length());
    }

    private void authenticateUser(String email, HttpServletRequest request) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (!optionalUser.isPresent()) {
            return;
        }

        User user = optionalUser.get();

        if (!user.isEnabled()) {
            return;
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                        )
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
