package com.dizzme.config;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.dizzme.repository.ClientRepository;
import com.dizzme.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ClientRepository clientRepository;

    @Value("${security.jwt.enabled:true}")
    private boolean jwtEnabled;

    @Value("${security.test.user.email:test@dizzme.com}")
    private String testUserEmail;

    @Value("${security.test.user.role:USER}")
    private String testUserRole;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        logger.info("Processing request: {} {}", request.getMethod(), path);
        logger.info("JWT enabled: {}", jwtEnabled);

        String normalizedPath = path.replace("/api", "");
        logger.debug("Normalized path: {}", normalizedPath);

        // URLs públicas - permite acesso sem JWT
        if (isPublicUrl(normalizedPath)) {
            logger.info("Public URL detected, skipping JWT validation: {}", path);

//            if (SecurityContextHolder.getContext().getAuthentication() == null) {
//                AnonymousAuthenticationToken anonymousAuth =
//                        new AnonymousAuthenticationToken(
//                                "anonymousUser",
//                                "anonymousUser",
//                                AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")
//                        );
//                SecurityContextHolder.getContext().setAuthentication(anonymousAuth);
//            }

            filterChain.doFilter(request, response);
            return;
        }

        // Se JWT estiver desabilitado, usa autenticação de teste
        if (!jwtEnabled) {
            setTestAuthentication();
            filterChain.doFilter(request, response);
            return;
        }

// URLs protegidas - requer JWT válido
        try {
            String token = getTokenFromRequest(request);

            if (token != null) {
                DecodedJWT decodedJWT = jwtService.validateToken(token);
                String email = decodedJWT.getSubject();

                var client = clientRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Client not found"));

                List<SimpleGrantedAuthority> authorities = Arrays.asList(
                        new SimpleGrantedAuthority("ROLE_" + client.getRole().name())
                );

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(client, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("JWT validated successfully for user: {}", email);
            }
        } catch (Exception e) {
            logger.error("JWT validation failed: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Verifica se a URL é pública e não requer autenticação
     * Remove o prefixo /api se existir para normalizar o path
     */
    private boolean isPublicUrl(String path) {
        String normalizedPath = path;
        while (normalizedPath.startsWith("/api")) {
            normalizedPath = normalizedPath.substring(4);
        }

        List<String> publicPaths = Arrays.asList(
                "/health",
                "/auth/login",
                "/auth/register",
                "/auth/create-admin",
                "/responses/submit"
        );
        return publicPaths.contains(normalizedPath)
                || normalizedPath.startsWith("/surveys/public/")
                || normalizedPath.startsWith("/qr/");

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