package com.coderiverside.quicknote;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class NoteLabelControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl(Long noteId) {
        return "http://localhost:" + port + "/notes/" + noteId + "/labels";
    }

    @Test
    void shouldListLabelsForNote() {
        // Nota 1 de sophia tiene etiquetas asociadas en los datos iniciales
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("sophia", "Zaqwsx");
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Object[]> response = restTemplate.exchange(getBaseUrl(1L), HttpMethod.GET, entity,
                Object[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(Object[].class);
        // No validamos cantidad exacta porque depende de los datos iniciales
    }

    @Test
    void shouldAddLabelToNote() {
        // Agregar etiqueta 2 (Personal) a nota 1 (sophia)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("sophia", "Zaqwsx");
        Map<String, Object> body = Map.of("labelId", 2L);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Void> response = restTemplate.postForEntity(getBaseUrl(1L), entity, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).hasToString("/notes/1/labels/2");
    }

    @Test
    void shouldRemoveLabelFromNote() {
        // Primero agregamos la etiqueta para asegurar que existe
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("sophia", "Zaqwsx");
        Map<String, Object> body = Map.of("labelId", 2L);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(getBaseUrl(1L), entity, Void.class);

        // Ahora la eliminamos
        HttpEntity<Void> deleteEntity = new HttpEntity<>(headers);
        ResponseEntity<Void> response = restTemplate.exchange(getBaseUrl(1L) + "/2", HttpMethod.DELETE, deleteEntity,
                Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
