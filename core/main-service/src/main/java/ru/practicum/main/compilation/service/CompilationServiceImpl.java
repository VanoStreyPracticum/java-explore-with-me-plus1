package ru.practicum.main.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.dto.UpdateCompilationRequest;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.compilation.repository.CompilationRepository;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.NotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = Collections.emptyList();
        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            events = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
            if (events.size() != newCompilationDto.getEvents().size()) {
                throw new NotFoundException("Некоторые события не найдены");
            }
        }
        Compilation compilation = Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.getPinned() != null ? newCompilationDto.getPinned() : false)
                .events(events)
                .build();
        compilation = compilationRepository.save(compilation);
        log.info("Создана подборка: id={}, title={}", compilation.getId(), compilation.getTitle());
        return toDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка не найдена: id=" + compId));
        compilationRepository.delete(compilation);
        log.info("Удалена подборка: id={}", compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateRequest) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка не найдена: id=" + compId));

        if (updateRequest.getTitle() != null) {
            compilation.setTitle(updateRequest.getTitle());
        }
        if (updateRequest.getPinned() != null) {
            compilation.setPinned(updateRequest.getPinned());
        }
        if (updateRequest.getEvents() != null) {
            List<Event> events = eventRepository.findAllByIdIn(updateRequest.getEvents());
            if (events.size() != updateRequest.getEvents().size()) {
                throw new NotFoundException("Некоторые события не найдены");
            }
            compilation.setEvents(events);
        }
        compilation = compilationRepository.save(compilation);
        log.info("Обновлена подборка: id={}, title={}", compId, compilation.getTitle());
        return toDto(compilation);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationRepository.findAllByPinned(pinned, pageable).getContent();
        } else {
            compilations = compilationRepository.findAll(pageable).getContent();
        }
        return compilations.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка не найдена: id=" + compId));
        return toDto(compilation);
    }

    private CompilationDto toDto(Compilation compilation) {
        List<EventShortDto> eventShortDtos = compilation.getEvents() != null ?
                eventMapper.toEventShortDtoList(compilation.getEvents()) : Collections.emptyList();
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(eventShortDtos)
                .build();
    }
}
