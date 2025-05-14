package br.insper.prova;


import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // desabilita CSRF pois é API stateless
                .csrf(AbstractHttpConfigurer::disable)
                // configura CORS conforme desejar
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // regras de autorização
                .authorizeHttpRequests(auth -> auth
                        // permitir preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/tarefa/**").permitAll()
                        // GET em /tarefa só requere estar autenticado
                        .requestMatchers(HttpMethod.GET, "/tarefa/**").authenticated()
                        // criação e deleção só para ADMIN
                        .requestMatchers(HttpMethod.POST, "/tarefa/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/tarefa/**").hasRole("ADMIN")
                        // qualquer outra precisa de auth
                        .anyRequest().authenticated()
                )
                // configura o resource server JWT
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.addAllowedOriginPattern("*");
        cfg.setAllowedMethods(List.of("GET", "POST", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter rolesConverter = new JwtGrantedAuthoritiesConverter();
        // usa o claim customizado que você publicou no Auth0
        rolesConverter.setAuthoritiesClaimName("https://musica-insper.com/roles");
        // prefixa cada entry com "ROLE_"
        rolesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter conv = new JwtAuthenticationConverter();
        conv.setJwtGrantedAuthoritiesConverter(rolesConverter);
        return conv;
    }
}
