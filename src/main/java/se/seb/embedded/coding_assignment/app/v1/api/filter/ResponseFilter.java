/* (C)2024 */
package se.seb.embedded.coding_assignment.app.v1.api.filter;

import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Filter to set traceId in the response for traceability */
@Profile("!test")
@Component
@RequiredArgsConstructor
public class ResponseFilter extends OncePerRequestFilter {

    private final Tracer tracer;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        response.setHeader("trace-id", tracer.currentSpan().context().traceId());
        filterChain.doFilter(request, response);
    }
}
