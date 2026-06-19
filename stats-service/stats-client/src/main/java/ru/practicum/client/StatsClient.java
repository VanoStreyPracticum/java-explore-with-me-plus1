package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.DateTimeFormatConstants;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StatsClient {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern(DateTimeFormatConstants.DATE_TIME_PATTERN);

    private final String serverUrl;
    private final RestTemplate restTemplate;

    public StatsClient(@Value("${stats-server.url}") String serverUrl,
                       RestTemplate restTemplate) {
        this.serverUrl = serverUrl;
        this.restTemplate = restTemplate;
    }

    public void hit(EndpointHitDto endpointHitDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EndpointHitDto> request = new HttpEntity<>(endpointHitDto, headers);
        restTemplate.exchange(serverUrl + "/hit", HttpMethod.POST, request, Void.class);
    }

    public List<StatsResponseDto> getStats(StatsRequestDto requestDto) {
        Map<String, Object> params = new HashMap<>();
        params.put("start", requestDto.getStart().format(FORMATTER));
        params.put("end", requestDto.getEnd().format(FORMATTER));
        params.put("unique", requestDto.getUnique());

        StringBuilder uriTemplate = new StringBuilder(serverUrl + "/stats?start={start}&end={end}&unique={unique}");

        if (requestDto.getUris() != null && !requestDto.getUris().isEmpty()) {
            for (int i = 0; i < requestDto.getUris().size(); i++) {
                String key = "uris" + i;
                uriTemplate.append("&uris={").append(key).append("}");
                params.put(key, requestDto.getUris().get(i));
            }
        }

        ResponseEntity<List<StatsResponseDto>> response = restTemplate.exchange(
                uriTemplate.toString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<StatsResponseDto>>() {},
                params
        );
        return response.getBody();
    }
}
