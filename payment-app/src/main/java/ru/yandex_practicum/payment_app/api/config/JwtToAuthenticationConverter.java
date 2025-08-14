package ru.yandex_practicum.payment_app.api.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public class JwtToAuthenticationConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {
    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt source) {
        List<String> roles = getRolesFromJwt(source);

        JwtAuthenticationToken authentication = new JwtAuthenticationToken(source, roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList());

        return Mono.just(authentication);
    }

    private List<String> getRolesFromJwt(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        Map<String, Object> account = (Map<String, Object>) resourceAccess.get("account");
        return (List<String>) account.get("roles");
    }

}
