package katsapa.spring.productservice.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final FixedWindowRateLimiter fixedWindowRateLimiter;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String apiKey = request.getHeader("X-API-KEY");
        String client;
        if(apiKey != null && !apiKey.isBlank()){
            client = apiKey;
        } else{
            String remoteAddr = request.getRemoteAddr();
            client = (remoteAddr != null) ? remoteAddr : "unknown";
        }
        boolean allow = fixedWindowRateLimiter.allowRequest(
                client,
                10,
                Duration.ofMinutes(1)
        );
        if(!allow){
            response.setStatus(429);
            response.getWriter().write("Rate limiter exceeded");
            return;
        }
    }
}
