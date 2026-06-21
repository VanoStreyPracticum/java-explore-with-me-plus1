package ru.practicum.main.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.user.dto.NewUserRequest;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        List<UserDto> result = userRepository.findByIds(ids, pageable).getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        log.info("Получен список пользователей ids={}, from={}, size={}, count={}", ids, from, size, result.size());
        return result;
    }

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest newUserRequest) {
        if (userRepository.existsByEmail(newUserRequest.getEmail())) {
            throw new ConflictException("Пользователь с таким email уже существует");
        }
        User user = User.builder()
                .name(newUserRequest.getName())
                .email(newUserRequest.getEmail())
                .build();
        user = userRepository.save(user);
        log.info("Создан пользователь: id={}, name={}", user.getId(), user.getName());
        return toDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: id=" + userId));
        userRepository.delete(user);
        log.info("Удалён пользователь: id={}", userId);
    }

    private UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
