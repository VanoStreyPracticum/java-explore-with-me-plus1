package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.DateTimeFormatConstants;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP client for the Stats Service.
 * <p>
 * Provides methods to save a hit and to retrieve view statistics.
 */
@Component
@Slf4j
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

    /**
     * Saves information about a request to an endpoint (hit).
     *
     * @param endpointHitDto the hit data
     */
    public void hit(EndpointHitDto endpointHitDto) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<EndpointHitDto> request = new HttpEntity<>(endpointHitDto, headers);
            restTemplate.exchange(serverUrl + "/hit", HttpMethod.POST, request, Void.class);
            log.debug("Hit saved: uri={}, ip={}", endpointHitDto.getUri(), endpointHitDto.getIp());
        } catch (RestClientException e) {
            log.warn("Failed to save hit to stats server: {}", e.getMessage());
        }
    }

    /**
     * Retrieves view statistics for the given parameters.
     *
     * @param requestDto the request parameters (start, end, uris, unique)
     * @return list of {@link StatsResponseDto}, or an empty list on error
     */
    public List<StatsResponseDto> getStats(StatsRequestDto requestDto) {
        try {
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
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException e) {
            log.warn("Failed to retrieve stats from stats server: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
