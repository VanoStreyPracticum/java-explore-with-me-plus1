package ru.practicum.main.managedlocation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class NewManagedLocationDto {
    @NotBlank(message = "Название локации обязательно")
    private String name;
    @NotNull(message = "Широта обязательна")
    private Float lat;
    @NotNull(message = "Долгота обязательна")
    private Float lon;
}
