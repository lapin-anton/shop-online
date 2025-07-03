package ru.yandex_practicum.shoponline.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.HttpStatusReturningServerLogoutSuccessHandler;
import reactor.core.publisher.Mono;
import ru.yandex_practicum.shoponline.service.UserService;

import java.nio.file.AccessDeniedException;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .csrf().disable()
            .formLogin(Customizer.withDefaults())
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/", "/item/*", "/images/*").permitAll()
                .pathMatchers("/items/add", "/upload").hasRole("ADMIN")
                .anyExchange().authenticated()
            )
            .logout(logout ->
                    logout.logoutUrl("/logout")
                    .logoutSuccessHandler(new HttpStatusReturningServerLogoutSuccessHandler())
            )
            .exceptionHandling(handling -> handling
                    .accessDeniedHandler((exchange, denied) ->
                            Mono.error(new AccessDeniedException("Access Denied")))
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        return username -> userService.findByName(username).map(user ->
                User.withUsername(user.getName())
                        .password(passwordEncoder.encode(user.getPassword()))
                        .roles(user.getRole())
                        .build()
        );
    }

}
