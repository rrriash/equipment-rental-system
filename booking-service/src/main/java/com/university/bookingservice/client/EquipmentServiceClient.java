package com.university.bookingservice.client;

import com.university.bookingservice.exception.ExternalEquipmentNotFoundException;
import com.university.bookingservice.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class EquipmentServiceClient {

    private final RestTemplate restTemplate;

    @Value("${equipment-service.url}")
    private String equipmentServiceUrl;

    public EquipmentDto getEquipmentById(Long equipmentId) {
        String url = equipmentServiceUrl + "/equipment/" + equipmentId;
        try {
            return restTemplate.getForObject(url, EquipmentDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ExternalEquipmentNotFoundException(equipmentId);
        } catch (ResourceAccessException e) {
            throw new ExternalServiceException("equipment-service недоступен: " + e.getMessage());
        }
    }

    public void updateEquipmentStatus(Long equipmentId, String status) {
        String url = equipmentServiceUrl + "/equipment/" + equipmentId + "/status";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> body = Map.of("status", status);
            restTemplate.patchForObject(url, new HttpEntity<>(body, headers), Void.class);
        } catch (ResourceAccessException e) {
            throw new ExternalServiceException("equipment-service недоступен: " + e.getMessage());
        }
    }
}
