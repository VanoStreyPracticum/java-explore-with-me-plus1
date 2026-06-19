package ru.practicum.main.managedlocation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.managedlocation.dto.ManagedLocationDto;
import ru.practicum.main.managedlocation.dto.NewManagedLocationDto;
import ru.practicum.main.managedlocation.dto.UpdateManagedLocationDto;
import ru.practicum.main.managedlocation.service.ManagedLocationService;

import java.util.List;

@RestController
@RequestMapping("/admin/locations")
@RequiredArgsConstructor
@Slf4j
public class AdminManagedLocationController {

    private final ManagedLocationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ManagedLocationDto create(@Valid @RequestBody NewManagedLocationDto dto) {
        return service.create(dto);
    }

    @PatchMapping("/{locationId}")
    @ResponseStatus(HttpStatus.OK)
    public ManagedLocationDto update(@PathVariable Long locationId,
                                     @RequestBody UpdateManagedLocationDto dto) {
        if (locationId <= 0) {
            throw new ValidationException("locationId must be positive");
        }
        return service.update(locationId, dto);
    }

    @DeleteMapping("/{locationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long locationId) {
        if (locationId <= 0) {
            throw new ValidationException("locationId must be positive");
        }
        service.delete(locationId);
    }

    @GetMapping("/{locationId}")
    @ResponseStatus(HttpStatus.OK)
    public ManagedLocationDto getById(@PathVariable Long locationId) {
        if (locationId <= 0) {
            throw new ValidationException("locationId must be positive");
        }
        return service.getById(locationId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ManagedLocationDto> getAll(@RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        if (size <= 0) {
            throw new ValidationException("size must be positive");
        }
        if (from < 0) {
            throw new ValidationException("from must be >= 0");
        }
        return service.getAll(from, size);
    }
}
