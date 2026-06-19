package ru.practicum.main.managedlocation.service;

import ru.practicum.main.managedlocation.dto.ManagedLocationDto;
import ru.practicum.main.managedlocation.dto.NewManagedLocationDto;
import ru.practicum.main.managedlocation.dto.UpdateManagedLocationDto;

import java.util.List;

public interface ManagedLocationService {

    ManagedLocationDto create(NewManagedLocationDto dto);

    ManagedLocationDto update(Long locationId, UpdateManagedLocationDto dto);

    void delete(Long locationId);

    ManagedLocationDto getById(Long locationId);

    List<ManagedLocationDto> getAll(int from, int size);
}
