package com.enigmacamp.enigmaCademy.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    // Store buckets by IP address
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // Create a bucket for the IP address with a limit of 10 requests per minute
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Only apply rate limiting to auth endpoints
        if (request.getRequestURI().startsWith("/api/auth/")) {
            // Get client IP
            String ipAddress = getClientIP(request);

            // Get or create bucket for this IP
            Bucket bucket = buckets.computeIfAbsent(ipAddress, k -> createNewBucket());

            // Try to consume a token
            if (bucket.tryConsume(1)) {
                // Token consumed, continue request
                filterChain.doFilter(request, response);
            } else {
                // No tokens available, return 429 Too Many Requests
                log.warn("Rate limit exceeded for IP: {}", ipAddress);
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"statusCode\":429,\"message\":\"Rate limit exceeded. Please try again later.\"}");
            }
        } else {
            // Not an auth endpoint, let the request through
            filterChain.doFilter(request, response);
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // Get the first IP in case of multiple proxies
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}