package com.dizzme.config;

import com.dizzme.repository.ClientRepository;
import com.dizzme.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ClientRepository clientRepository;

    // Propriedade para controlar se JWT está habilitado
    @Value("${security.jwt.enabled:true}")
    private boolean jwtEnabled;

    // Email e role padrão para testes (quando JWT está desabilitado)
    @Value("${security.test.user.email:test@dizzme.com}")
    private String testUserEmail;

    @Value("${security.test.user.role:USER}")
    private String testUserRole;

    // Lista de URLs públicas que NÃO precisam de autenticação
    private static final List<String> PUBLIC_URLS = Arrays.asList(
            "/api/health",
            "/api/health/version",
            "/actuator/health",
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/create-admin",
            "/api/responses/submit"
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        logger.info("Processing request: " + request.getMethod() + " " + path);
        logger.info("JWT enabled: " + jwtEnabled);

        // Permitir acesso direto a URLs públicas SEM validação de JWT
        if (isPublicUrl(path)) {
            logger.info("Public URL detected, skipping JWT validation: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        // Se JWT estiver desabilitado, configura um usuário de teste e continua
        if (!jwtEnabled) {
            setTestAuthentication();
            filterChain.doFilter(request, response);
            return;
        }

        // Lógica normal do JWT
        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token)) {
            try {
                com.auth0.jwt.interfaces.DecodedJWT decodedJWT = jwtService.validateToken(token);
                String email = decodedJWT.getSubject();
                String role = decodedJWT.getClaim("role").asString();

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Create authorities based on role
                    List<SimpleGrantedAuthority> authorities = Arrays.asList(
                            new SimpleGrantedAuthority("ROLE_" + role)
                    );

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(email, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                logger.error("Cannot set user authentication: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Verifica se a URL é pública e não requer autenticação
     */
    private boolean isPublicUrl(String path) {
        // Verifica URLs exatas
        if (PUBLIC_URLS.contains(path)) {
            return true;
        }

        // Verifica padrões com wildcard
        if (path.startsWith("/api/surveys/public/") ||
                path.startsWith("/api/qr/")) {
            return true;
        }

        return false;
    }

    /**
     * Configura uma autenticação de teste quando JWT está desabilitado
     */
    private void setTestAuthentication() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            List<SimpleGrantedAuthority> authorities = Arrays.asList(
                    new SimpleGrantedAuthority("ROLE_" + testUserRole)
            );

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(testUserEmail, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authToken);

            logger.info("JWT disabled - Using test authentication for user: " + testUserEmail +
                    " with role: " + testUserRole);
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}