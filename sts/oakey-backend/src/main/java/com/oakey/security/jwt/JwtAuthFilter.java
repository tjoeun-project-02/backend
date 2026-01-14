package com.oakey.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        // 🔥 1. 인증 제외 경로
        if (
                path.startsWith("/auth") ||
                path.startsWith("/swagger") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/h2-console")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        // 🔥 2. JWT 인증 처리
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);

            if (jwtProvider.isValid(token)
                    && "access".equals(jwtProvider.getType(token))) {

                Long userId = jwtProvider.getUserId(token);
                SecurityContextHolder.getContext()
                        .setAuthentication(new JwtAuthentication(userId));
            }
        }

        filterChain.doFilter(request, response);
    }
}
