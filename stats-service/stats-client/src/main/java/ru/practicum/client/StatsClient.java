package ru.practicum.client;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.DateTimeFormatConstants;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class StatsClient {

    private static final String STATS_SERVICE_ID = "stats-service";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern(DateTimeFormatConstants.DATE_TIME_PATTERN);

    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate;

    public StatsClient(DiscoveryClient discoveryClient, RestTemplate restTemplate) {
        this.discoveryClient = discoveryClient;
        this.restTemplate = restTemplate;
    }

    public void hit(EndpointHitDto endpointHitDto) {
        URI uri = makeUri("/hit");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EndpointHitDto> request = new HttpEntity<>(endpointHitDto, headers);
        restTemplate.exchange(uri, HttpMethod.POST, request, Void.class);
    }

    public List<StatsResponseDto> getStats(StatsRequestDto requestDto) {
        String startEncoded = URLEncoder.encode(requestDto.getStart().format(FORMATTER), StandardCharsets.UTF_8);
        String endEncoded = URLEncoder.encode(requestDto.getEnd().format(FORMATTER), StandardCharsets.UTF_8);

        URI uri = makeUri("/stats");
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(uri)
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

    private URI makeUri(String path) {
        List<ServiceInstance> instances = discoveryClient.getInstances(STATS_SERVICE_ID);
        if (instances.isEmpty()) {
            throw new RuntimeException("No instances of " + STATS_SERVICE_ID + " found in Discovery");
        }
        ServiceInstance instance = instances.get(0);
        return URI.create("http://" + instance.getHost() + ":" + instance.getPort() + path);
    }
}
