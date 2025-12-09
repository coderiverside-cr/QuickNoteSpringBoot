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
class NoteSettingsControllerIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnNoteSettings() {
        ResponseEntity<NoteSettingsDto> response = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .getForEntity("/notes/1/settings", NoteSettingsDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(1L);
    }

    @Test
    void shouldReturnNotFoundForUnknownNoteSettings() {
        ResponseEntity<ErrorResponse> response = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .getForEntity("/notes/999/settings", ErrorResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DirtiesContext
    void shouldCreateNoteSettings() {
        // Use note 8, which does not have initial settings and is owned by sophia
        NoteSettingsDto newSettings = new NoteSettingsDto(8L, false, "HIGH", true);
        ResponseEntity<Void> createResponse = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .postForEntity("/notes/8/settings", newSettings, Void.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        URI location = createResponse.getHeaders().getLocation();
        ResponseEntity<NoteSettingsDto> getResponse = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .getForEntity(location, NoteSettingsDto.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().priority()).isEqualTo("HIGH");
    }

    @Test
    @DirtiesContext
    void shouldUpdateNoteSettings() {
        NoteSettingsDto updatedSettings = new NoteSettingsDto(1L, true, "MEDIUM", false);
        HttpEntity<NoteSettingsDto> requestEntity = new HttpEntity<>(updatedSettings);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .exchange("/notes/1/settings", HttpMethod.PUT, requestEntity, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        ResponseEntity<NoteSettingsDto> getResponse = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .getForEntity("/notes/1/settings", NoteSettingsDto.class);
        assertThat(getResponse.getBody().locked()).isTrue();
        assertThat(getResponse.getBody().priority()).isEqualTo("MEDIUM");
        assertThat(getResponse.getBody().enableSharing()).isFalse();
    }

    @Test
    @DirtiesContext
    void shouldDeleteNoteSettings() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .exchange("/notes/1/settings", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        ResponseEntity<ErrorResponse> getResponse = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .getForEntity("/notes/1/settings", ErrorResponse.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
