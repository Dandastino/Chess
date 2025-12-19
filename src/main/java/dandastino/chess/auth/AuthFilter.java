package dandastino.chess.auth;

import dandastino.chess.exceptions.UnauthorizeException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter {
    @Autowired
    private AuthTools authComponent;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // Authentication
            String token = request.getHeader("Authorization");

            if (token == null || !token.startsWith("Bearer ")) {throw new UnauthorizeException("Invalid token");}

            String accessToken = token.replace("Bearer", "");

            authComponent.validateToken(accessToken);

            // Authorization

            filterChain.doFilter(request, response);

        }catch (UnauthorizeException e){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "problem with the token: " + e.getMessage());
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return new AntPathMatcher().match("/api/auth/**", request.getServletPath());
    }
}
