package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.DateTimeFormatConstants;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class StatsClient {

    private final String serverUrl;
    private final RestTemplate restTemplate;
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern(DateTimeFormatConstants.DATE_TIME_PATTERN);

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
        String startEncoded = URLEncoder.encode(requestDto.getStart().format(FORMATTER), StandardCharsets.UTF_8);
        String endEncoded = URLEncoder.encode(requestDto.getEnd().format(FORMATTER), StandardCharsets.UTF_8);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serverUrl + "/stats")
                .queryParam("start", startEncoded)
                .queryParam("end", endEncoded)
                .queryParam("unique", requestDto.getUnique());

        if (requestDto.getUris() != null && !requestDto.getUris().isEmpty()) {
            builder.queryParam("uris", String.join(",", requestDto.getUris()));
        }

        ResponseEntity<List<StatsResponseDto>> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<StatsResponseDto>>() {}
        );
        return response.getBody();
    }
}
