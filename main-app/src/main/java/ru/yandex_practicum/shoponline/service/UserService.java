package ru.yandex_practicum.shoponline.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex_practicum.shoponline.model.entity.User;
import ru.yandex_practicum.shoponline.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Mono<User> findByName(String name) {
        return userRepository.findByName(name).switchIfEmpty(Mono.empty());
    }

}
