package com.coderiverside.quicknote;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONArray;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class QuickNoteApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnANote() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("coderiverside", "Zaqwsx")
                .getForEntity("/notes/1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");

        assertThat(id).isNotNull();
        assertThat(id).isEqualTo(1);

        String title = documentContext.read("@.title");
        assertThat(title).isNotNull();
        assertThat(title).isEqualTo("Grocery List");
    }

    @Test
    void shouldNotReturnANoteWithAnUnknownId() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("coderiverside", "Zaqwsx")
                .getForEntity("/notes/25", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();

    }

    @Test
    void shouldCreateANewNote() {
        NoteDto newNote = new NoteDto(
                0L,
                "Meeting Notes",
                "Discuss project roadmap and milestones",
                "text",
                null,
                false,
                false,
                "blue");

        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("coderiverside", "Zaqwsx")
                .postForEntity("/notes", newNote, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfNewCashCard = response.getHeaders().getLocation();

        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("coderiverside", "Zaqwsx")
                .getForEntity(locationOfNewCashCard, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void shouldReturnAllNotes() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("coderiverside", "Zaqwsx")
                .getForEntity("/notes", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int size = documentContext.read("$.size()");
        assertThat(size).isEqualTo(7);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).isNotEmpty();
        assertThat(ids).containsExactlyInAnyOrder(1, 2, 3, 4, 5, 6, 7);

        JSONArray titles = documentContext.read("$..title");
        assertThat(titles).isNotEmpty();
        assertThat(titles).containsExactlyInAnyOrder(
                "Grocery List",
                "Buy groceries",
                "Gym workout",
                "Meeting notes",
                "Shopping list",
                "Project ideas",
                "Book recommendations");

    }

    @Test
    void shouldReturnNotesWithPagination() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("coderiverside", "Zaqwsx")
                .getForEntity("/notes?page=0&size=3", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int size = documentContext.read("$.size()");
        assertThat(size).isEqualTo(3);

        JSONArray page = documentContext.read("$[*]");
        assertThat(page).isNotEmpty();
        assertThat(page.size()).isEqualTo(3);

    }

    @Test
    void shouldReturnNotesWithSorting() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("coderiverside", "Zaqwsx")
                .getForEntity("/notes?page=0&size=3&sort=title,desc",
                        String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray read = documentContext.read("$[*]");
        assertThat(read).isNotEmpty();
        assertThat(read.size()).isEqualTo(3);

        String firstTitle = documentContext.read("$[0].title");
        assertThat(firstTitle).isEqualTo("Shopping list");

        String lastTitle = documentContext.read("$[2].title");
        assertThat(lastTitle).isEqualTo("Meeting notes");
    }

    @Test
    void shouldReturnASortedPageOfNotesWithDefaultValues() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("coderiverside", "Zaqwsx")
                .getForEntity("/notes", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray read = documentContext.read("$[*]");
        assertThat(read).isNotEmpty();
        assertThat(read.size()).isEqualTo(7);

        String firstTitle = documentContext.read("$[0].title");
        assertThat(firstTitle).isEqualTo("Shopping list");

        String lastTitle = documentContext.read("$[6].title");
        assertThat(lastTitle).isEqualTo("Book recommendations");
    }

    @Test
    void shouldNoReturnANoteWhenNotAuthenticated() {
        ResponseEntity<String> response = restTemplate
        .withBasicAuth("coderiverside", "wrongpassword")
        .getForEntity("/notes/1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isBlank();
    }


    @Test
    void shoudRejectUsersWhoAreNotNoteOwners() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("no-notes", "Zaq1")
                .getForEntity("/notes/1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isBlank();
    }

}
