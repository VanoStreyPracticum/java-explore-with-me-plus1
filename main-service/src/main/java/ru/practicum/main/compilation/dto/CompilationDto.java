package ru.practicum.main.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main.event.dto.EventShortDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationDto {
    private Long id;
    private String title;
    private Boolean pinned;
    private List<EventShortDto> events;
}
