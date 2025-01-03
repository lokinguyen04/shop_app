package com.project.shopapp.configurations;

import com.project.shopapp.filters.JwtTokenFilter;
import com.project.shopapp.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
//@EnableMethodSecurity
@RequiredArgsConstructor
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebMvc
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    @Value("${api.prefix}")
    private String apiPrefix;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                String.format("%s/users/register", apiPrefix),
                                String.format("%s/users/login", apiPrefix)
                        )
                        .permitAll()
                        .requestMatchers(GET, String.format("%s/roles**", apiPrefix)).permitAll()

                        .requestMatchers(GET, String.format("%s/categories**", apiPrefix)).permitAll()
                        .requestMatchers(GET, String.format("%s/categories/**", apiPrefix)).permitAll()
                        .requestMatchers(POST, String.format("%s/categories", apiPrefix)).hasRole(Role.ADMIN)
                        .requestMatchers(PUT, String.format("%s/categories/**", apiPrefix)).hasRole(Role.ADMIN)
                        .requestMatchers(DELETE, String.format("%s/categories/**", apiPrefix)).hasRole(Role.ADMIN)

                        .requestMatchers(GET, String.format("%s/products**", apiPrefix)).permitAll()
                        .requestMatchers(GET, String.format("%s/products/**", apiPrefix)).permitAll()
                        .requestMatchers(GET, String.format("%s/products/images/*", apiPrefix)).permitAll()
                        .requestMatchers(POST, String.format("%s/products**", apiPrefix)).hasRole(Role.ADMIN)
                        .requestMatchers(PUT, String.format("%s/products/**", apiPrefix)).hasRole(Role.ADMIN)
                        .requestMatchers(DELETE, String.format("%s/products/**", apiPrefix)).hasAnyRole(Role.ADMIN)

                        .requestMatchers(GET, String.format("%s/orders/**", apiPrefix)).permitAll()
                        .requestMatchers(POST, String.format("%s/orders/**", apiPrefix)).hasRole(Role.USER)
                        .requestMatchers(PUT, String.format("%s/orders/**", apiPrefix)).hasRole(Role.ADMIN)
                        .requestMatchers(DELETE, String.format("%s/orders/**", apiPrefix)).hasRole(Role.ADMIN)

                        .requestMatchers(GET, String.format("%s/orders_detail/**", apiPrefix)).hasAnyRole(Role.USER, Role.ADMIN)
                        .requestMatchers(POST, String.format("%s/orders_detail/**", apiPrefix)).hasRole(Role.USER)
                        .requestMatchers(PUT, String.format("%s/orders_detail/**", apiPrefix)).hasRole(Role.ADMIN)
                        .requestMatchers(DELETE, String.format("%s/orders_detail/**", apiPrefix)).hasRole(Role.ADMIN)

                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable);

        http.cors(httpSecurityCorsConfigurer -> {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowedOrigins(List.of("*"));
            corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
            corsConfiguration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
            corsConfiguration.setExposedHeaders(List.of("x-auth-token"));
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", corsConfiguration);
            httpSecurityCorsConfigurer.configurationSource(source);
        });

        return http.build();
    }
}
