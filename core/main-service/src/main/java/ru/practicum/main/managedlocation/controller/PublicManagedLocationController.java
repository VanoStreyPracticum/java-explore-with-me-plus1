package ru.practicum.main.managedlocation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.managedlocation.dto.ManagedLocationDto;
import ru.practicum.main.managedlocation.service.ManagedLocationService;

import java.util.List;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
@Slf4j
public class PublicManagedLocationController {

    private final ManagedLocationService service;

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

    @GetMapping("/{locationId}")
    @ResponseStatus(HttpStatus.OK)
    public ManagedLocationDto getById(@PathVariable Long locationId) {
        if (locationId <= 0) {
            throw new ValidationException("locationId must be positive");
        }
        return service.getById(locationId);
    }
}
