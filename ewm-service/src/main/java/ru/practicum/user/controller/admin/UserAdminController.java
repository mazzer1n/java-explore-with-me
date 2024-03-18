package ru.practicum.user.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.*;
import ru.practicum.user.service.UserServiceInterface;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
public class UserAdminController {
    private final UserServiceInterface userServiceInterface;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> findAll(
        @RequestParam(value = "ids", required = false) List<Long> ids,
        @PositiveOrZero @RequestParam(defaultValue = "0", required = false) Integer from,
        @Positive @RequestParam(defaultValue = "10", required = false) Integer size) {
        log.info("Получение информации о пользователях");

        return userServiceInterface.findAll(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto save(@RequestBody @Valid NewUserRequest dto) {
        log.info("Добавление нового пользователя");

        return userServiceInterface.save(dto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        log.info("Удаление пользователя");
        userServiceInterface.delete(userId);
    }
}