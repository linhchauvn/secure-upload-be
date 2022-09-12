package com.tenerity.nordic.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MySecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${ngAuth.tenantId}")
    private String ngAuthTenantId;
    @Value("${ngAuth.jwk-set-uri}")
    private String jwkSetUri;
    @Value("${basicAuth.username}")
    private String username;
    @Value("${basicAuth.password}")
    private String password;

    private String[] openEndpoint = new String[]{"/actuator/health", "/login", "/login/**", "/document/**/download"};

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().configurationSource(item -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.addAllowedOrigin(CorsConfiguration.ALL);
                    config.addAllowedMethod(CorsConfiguration.ALL);
                    config.addAllowedHeader(CorsConfiguration.ALL);
                    return config;
                })
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().csrf().disable()
                .authorizeRequests()
                .antMatchers(openEndpoint).permitAll()
                .anyRequest().authenticated().and()
                .httpBasic().and()
                .oauth2ResourceServer().jwt()
                .decoder(nimbusJwtDecoder())
                .jwtAuthenticationConverter(oAuthJwtAuthenticationConverter())
        ;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(staticJwtAuthenticationProvider());

        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        auth.inMemoryAuthentication()
                .withUser(username).password(encoder.encode(password))
                .authorities("ROLE_ADMIN");
    }

    @Bean
    OAuthJwtAuthenticationConverter oAuthJwtAuthenticationConverter() {
        return new OAuthJwtAuthenticationConverter();
    }

    @Bean
    StaticJwtAuthenticationProvider staticJwtAuthenticationProvider() {
        return new StaticJwtAuthenticationProvider(new StaticJwtAuthenticationConverter());
    }

    @Bean
    public NimbusJwtDecoder nimbusJwtDecoder(){
        RestTemplate rest = new RestTemplate();
        rest.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("tenant-id", ngAuthTenantId);
            return execution.execute(request, body);
        });
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).restOperations(rest).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
