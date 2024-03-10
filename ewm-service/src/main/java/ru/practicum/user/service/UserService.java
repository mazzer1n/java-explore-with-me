package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.exceptions.UserNotFoundException;
import ru.practicum.user.dto.*;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.user.dto.UserMapper.*;

@Service
@RequiredArgsConstructor
public class UserService implements UserServiceInterface {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll(List<Long> ids, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        List<UserDto> result;

        if (ids != null) {
            result = userRepository.findByIdIn(ids, page).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
        } else {
            result = userRepository.findAll(page).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
        }

        return result;
    }

    @Override
    @Transactional
    public UserDto save(NewUserRequest dto) {
        return toUserDto(userRepository.save(toUserFromRequest(dto)));
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        getExistingUser(userId);
        userRepository.deleteById(userId);
    }

    public User getExistingUser(long userId) {
        return userRepository.findById(userId).orElseThrow(
            () -> new UserNotFoundException("Пользователь с id " + userId + " не найден")
        );
    }
}