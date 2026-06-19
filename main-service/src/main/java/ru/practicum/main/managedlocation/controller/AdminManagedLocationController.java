package ru.practicum.main.managedlocation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
        log.info("POST /admin/locations");
        return service.create(dto);
    }

    @PatchMapping("/{locationId}")
    @ResponseStatus(HttpStatus.OK)
    public ManagedLocationDto update(@PathVariable Long locationId,
                                     @RequestBody UpdateManagedLocationDto dto) {
        log.info("PATCH /admin/locations/{}", locationId);
        return service.update(locationId, dto);
    }

    @DeleteMapping("/{locationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long locationId) {
        log.info("DELETE /admin/locations/{}", locationId);
        service.delete(locationId);
    }

    @GetMapping("/{locationId}")
    @ResponseStatus(HttpStatus.OK)
    public ManagedLocationDto getById(@PathVariable Long locationId) {
        log.info("GET /admin/locations/{}", locationId);
        return service.getById(locationId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ManagedLocationDto> getAll(@RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        log.info("GET /admin/locations?from={}&size={}", from, size);
        return service.getAll(from, size);
    }
}
