package ru.practicum.main.managedlocation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.managedlocation.dto.ManagedLocationDto;
import ru.practicum.main.managedlocation.dto.NewManagedLocationDto;
import ru.practicum.main.managedlocation.dto.UpdateManagedLocationDto;
import ru.practicum.main.managedlocation.model.ManagedLocation;
import ru.practicum.main.managedlocation.repository.ManagedLocationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ManagedLocationServiceImpl implements ManagedLocationService {

    private final ManagedLocationRepository repository;

    @Override
    @Transactional
    public ManagedLocationDto create(NewManagedLocationDto dto) {
        ManagedLocation location = ManagedLocation.builder()
                .name(dto.getName())
                .lat(dto.getLat())
                .lon(dto.getLon())
                .build();
        location = repository.save(location);
        log.info("Создана управляемая локация: id={}, name={}", location.getId(), location.getName());
        return toDto(location);
    }

    @Override
    @Transactional
    public ManagedLocationDto update(Long locationId, UpdateManagedLocationDto dto) {
        ManagedLocation location = repository.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Локация не найдена: id=" + locationId));
        if (dto.getName() != null) location.setName(dto.getName());
        if (dto.getLat() != null) location.setLat(dto.getLat());
        if (dto.getLon() != null) location.setLon(dto.getLon());
        location = repository.save(location);
        log.info("Обновлена локация: id={}", locationId);
        return toDto(location);
    }

    @Override
    @Transactional
    public void delete(Long locationId) {
        ManagedLocation location = repository.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Локация не найдена: id=" + locationId));
        repository.delete(location);
        log.info("Удалена локация: id={}", locationId);
    }

    @Override
    public ManagedLocationDto getById(Long locationId) {
        ManagedLocation location = repository.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Локация не найдена: id=" + locationId));
        log.info("Получена локация id={}", locationId);
        return toDto(location);
    }

    @Override
    public List<ManagedLocationDto> getAll(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<ManagedLocationDto> result = repository.findAll(pageable).getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        log.info("Получен список локаций from={}, size={}, count={}", from, size, result.size());
        return result;
    }

    private ManagedLocationDto toDto(ManagedLocation location) {
        return ManagedLocationDto.builder()
                .id(location.getId())
                .name(location.getName())
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
