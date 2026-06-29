package com.university.bookingservice.client;

import com.university.bookingservice.exception.ExternalServiceException;
import com.university.bookingservice.exception.ExternalUserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final RestTemplate restTemplate;

    @Value("${user-service.url}")
    private String userServiceUrl;

    public UserDto getUserById(Long userId) {
        String url = userServiceUrl + "/users/" + userId;
        try {
            return restTemplate.getForObject(url, UserDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ExternalUserNotFoundException(userId);
        } catch (ResourceAccessException e) {
            throw new ExternalServiceException("user-service недоступен: " + e.getMessage());
        }
    }

    public void unblockUser(Long userId) {
        String url = userServiceUrl + "/users/" + userId + "/unblock";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            restTemplate.patchForObject(url, new HttpEntity<>(headers), Void.class);
        } catch (ResourceAccessException e) {
            throw new ExternalServiceException("user-service недоступен: " + e.getMessage());
        }
    }
}
