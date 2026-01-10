package com.coderiverside.quicknote;

import com.coderiverside.quicknote.config.AppInfo;
import com.coderiverside.quicknote.config.FeatureDetail;
import com.coderiverside.quicknote.config.SystemItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConfigControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnAppInfo() {
        ResponseEntity<AppInfo> response = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .exchange("/api/config/info", HttpMethod.GET, null, AppInfo.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("QuickNote App");
        assertThat(response.getBody().version()).isEqualTo("1.0.1");
    }

    @Test
    void shouldReturnFeatures() {
        ResponseEntity<Map<String, FeatureDetail>> response = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .exchange(
                        "/api/config/features",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Map<String, FeatureDetail>>() {
                        });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsKey("beta-mode");
        assertThat(response.getBody().get("beta-mode").enabled()).isTrue();
        assertThat(response.getBody().get("beta-mode").description()).isNotEmpty();
    }

    @Test
    void shouldReturnList() {
        ResponseEntity<List<SystemItem>> response = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .exchange(
                        "/api/config/list",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<SystemItem>>() {
                        });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(3);
        assertThat(response.getBody().get(0).name()).isEqualTo("LocalNetwork");
        assertThat(response.getBody().get(0).priority()).isEqualTo(1);
    }

    @Test
    void shouldReturnWelcomeMessage() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("sophia", "Zaqwsx")
                .getForEntity("/api/config/message", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Welcome to QuickNote! Enjoy your stay.");
    }
}