package com.coderiverside.quicknote;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
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
class LabelControllerIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnAllLabels() {
        ResponseEntity<List<LabelDto>> response = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .exchange(
                        "/labels",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<LabelDto>>() {
                        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        List<LabelDto> labels = response.getBody();
        assertThat(labels).isNotEmpty();
        assertThat(labels).hasSize(2);
    }

    @Test
    @DirtiesContext
    void shouldCreateLabel() {
        LabelDto newLabel = new LabelDto(null, "Important", "sophia");
        ResponseEntity<Void> createResponse = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .postForEntity("/labels", newLabel, Void.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        URI location = createResponse.getHeaders().getLocation();
        ResponseEntity<LabelDto> getResponse = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .getForEntity(location, LabelDto.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().name()).isEqualTo("Important");
    }

    @Test
    void shouldNotCreateDuplicateLabel() {
        LabelDto label = new LabelDto(null, "Work", "sophia");
        ResponseEntity<Void> createResponse = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .postForEntity("/labels", label, Void.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturnLabelById() {
        ResponseEntity<LabelDto> response = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .getForEntity("/labels/1", LabelDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().name()).isEqualTo("Work");
    }

    @Test
    void shouldReturnNotFoundForUnknownLabel() {
        ResponseEntity<ErrorResponse> response = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .getForEntity("/labels/999", ErrorResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DirtiesContext
    void shouldUpdateLabel() {
        LabelDto updatedLabel = new LabelDto(1L, "UpdatedLabel", "sophia");
        HttpEntity<LabelDto> requestEntity = new HttpEntity<>(updatedLabel);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .exchange("/labels/1", HttpMethod.PUT, requestEntity, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        ResponseEntity<LabelDto> getResponse = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .getForEntity("/labels/1", LabelDto.class);
        assertThat(getResponse.getBody().name()).isEqualTo("UpdatedLabel");
    }

    @Test
    @DirtiesContext
    void shouldDeleteLabel() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .exchange("/labels/1", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        ResponseEntity<ErrorResponse> getResponse = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .getForEntity("/labels/1", ErrorResponse.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
