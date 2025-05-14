package br.insper.prova;

import com.auth0.spring.security.api.JwtWebSecurityConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    public SecurityConfig(
            @Value("${auth0.audience}") String audience,
            @Value("${auth0.domain}") String domain
    ) {
        JwtWebSecurityConfigurer
                .forRS256(audience, domain)
                .configure(new HttpSecurity().csrf().disable());
    }
}