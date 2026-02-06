package dandastino.chess.auth;

import dandastino.chess.exceptions.UnauthorizeException;
import dandastino.chess.users.User;
import dandastino.chess.users.UsersRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class AuthFilter extends OncePerRequestFilter {
    @Autowired
    private AuthTools authComponent;
    @Autowired
    private UsersRepository usersRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {throw new UnauthorizeException("Invalid token");}
            String accessToken = token.replace("Bearer ", "");
            authComponent.validateToken(accessToken);

            UUID userId = authComponent.getUserIdFromToken(accessToken);
            User found = usersRepository.findById(userId).orElseThrow(() -> new UnauthorizeException("User not found"));
            Authentication authentication = new UsernamePasswordAuthenticationToken(found, null, found.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (UnauthorizeException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "problem with the token: " + e.getMessage());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        AntPathMatcher matcher = new AntPathMatcher();
        String path = request.getServletPath();
        if (matcher.match("/auth/**", path)) {
            return true;
        }
        // Allow POST /users (registration) without token
        return "POST".equalsIgnoreCase(request.getMethod()) && matcher.match("/users", path);
    }
}
