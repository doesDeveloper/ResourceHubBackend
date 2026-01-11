package com.ahmad.resourcehub.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class FrameEmbedFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (uri.startsWith("/api/files/uploads/")) {
            response.addHeader("Content-Security-Policy", "frame-ancestors *");
        } else {
            response.addHeader("X-Frame-Options", "DENY");
        }

        filterChain.doFilter(request, response);
    }
}
