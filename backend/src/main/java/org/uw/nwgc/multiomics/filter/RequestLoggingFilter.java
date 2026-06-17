package org.uw.nwgc.multiomics.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class RequestLoggingFilter {
//public class RequestLoggingFilter extends OncePerRequestFilter {

//    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
//        System.out.println("Incoming request: " + request.getMethod() + " " + request.getRequestURI());
        filterChain.doFilter(request, response);
    }
}