package ru.practicum.main.managedlocation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagedLocationDto {
    private Long id;
    private String name;
    private Float lat;
    private Float lon;
}
