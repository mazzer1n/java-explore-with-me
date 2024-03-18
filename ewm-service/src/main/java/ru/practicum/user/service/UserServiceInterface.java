package ru.practicum.user.service;

import ru.practicum.user.dto.*;

import java.util.List;

public interface UserServiceInterface {
    List<UserDto> findAll(List<Long> ids, Integer from, Integer size);

    UserDto save(NewUserRequest dto);

    void delete(Long userId);
}