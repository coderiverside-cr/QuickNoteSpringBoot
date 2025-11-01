package com.coderiverside.quicknote;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import com.coderiverside.quicknote.exception.ErrorResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReminderControllerIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnAllRemindersForNote() {
        ResponseEntity<List<ReminderDto>> response = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .exchange(
                        "/notes/1/reminders",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<ReminderDto>>() {
                        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DirtiesContext
    void shouldCreateReminder() {
        ReminderDto newReminder = new ReminderDto(null, LocalDateTime.of(2025, 11, 1, 10, 0), "sophia");
        ResponseEntity<Void> createResponse = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .postForEntity("/notes/1/reminders", newReminder, Void.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        URI location = createResponse.getHeaders().getLocation();
        ResponseEntity<ReminderDto> getResponse = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .getForEntity(location, ReminderDto.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().remindAt()).isEqualTo(LocalDateTime.of(2025, 11, 1, 10, 0));
    }

    @Test
    void shouldReturnReminderById() {
        ResponseEntity<ReminderDto> response = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .getForEntity("/notes/1/reminders/1", ReminderDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(1L);
    }

    @Test
    void shouldReturnNotFoundForUnknownReminder() {
        ResponseEntity<ErrorResponse> response = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .getForEntity("/notes/1/reminders/999", ErrorResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DirtiesContext
    void shouldUpdateReminder() {
        ReminderDto updatedReminder = new ReminderDto(1L, LocalDateTime.of(2025, 12, 1, 10, 0), "sophia");
        HttpEntity<ReminderDto> requestEntity = new HttpEntity<>(updatedReminder);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .exchange("/notes/1/reminders/1", HttpMethod.PUT, requestEntity, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        ResponseEntity<ReminderDto> getResponse = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .getForEntity("/notes/1/reminders/1", ReminderDto.class);
        assertThat(getResponse.getBody().remindAt()).isEqualTo(LocalDateTime.of(2025, 12, 1, 10, 0));
    }

    @Test
    @DirtiesContext
    void shouldDeleteReminder() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .exchange("/notes/1/reminders/1", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        ResponseEntity<ErrorResponse> getResponse = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .getForEntity("/notes/1/reminders/1", ErrorResponse.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
