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
// @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class QuicknoteApplicationTests {

	@Autowired
	TestRestTemplate restTemplate;

	@Test
	void shouldReturnANote() {
		ResponseEntity<NoteDto> response = restTemplate
				.withBasicAuth("sophia", "Zaqwsx")
				.getForEntity("/notes/1", NoteDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		assertThat(response.getBody()).isNotNull();

		NoteDto note = response.getBody();
		assertThat(note.id()).isEqualTo(1L);
		assertThat(note.title()).isEqualTo("Grocery List");

	}

	@Test
	void shouldNotReturnANoteWhenUsingBadCredentials() {
		ResponseEntity<NoteDto> response = restTemplate
				.withBasicAuth("sophia", "wrongpassword")
				.getForEntity("/notes/1", NoteDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

		response = restTemplate
				.withBasicAuth("unknownuser", "Zaqwsx")
				.getForEntity("/notes/1", NoteDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

	}

	@Test
	void shouldRejectUsersWhoAreNotNoteOwners() {
		ResponseEntity<NoteDto> response = restTemplate
				.withBasicAuth("no-notes", "Zaq1")
				.getForEntity("/notes/1", NoteDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	void shouldNotReturnANoteWithAnUnknownId() {
		ResponseEntity<ErrorResponse> response = restTemplate
				.withBasicAuth("sophia", "Zaqwsx")
				.getForEntity("/notes/25", ErrorResponse.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody().message())
				.isEqualTo("Resource with ID 25 not found.");

	}

	@Test
	@DirtiesContext
	void shouldCreateANote() {
		NoteDto newNote = new NoteDto(
				null,
				"Meeting Notes",
				"Discuss project roadmap and milestones",
				"text",
				null,
				false,
				false,
				"#0000FF",
				"sophia");
		ResponseEntity<Void> createResponse = restTemplate
				.withBasicAuth("sophia", "Zaqwsx")
				.postForEntity("/notes", newNote, Void.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		URI locationOfNote = createResponse.getHeaders().getLocation();
		ResponseEntity<NoteDto> getResponse = restTemplate
				.withBasicAuth("sophia", "Zaqwsx")
				.getForEntity(locationOfNote, NoteDto.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

	}

	@Test
	void shouldReturnAllNotes() {
		ResponseEntity<List<NoteDto>> response = restTemplate
				.withBasicAuth("sophia", "Zaqwsx")
				.exchange(
						"/notes",
						HttpMethod.GET,
						null,
						new ParameterizedTypeReference<List<NoteDto>>() {
						});
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		assertThat(response.getBody()).isNotNull();

		List<NoteDto> notes = response.getBody();
		assertThat(notes).isNotNull();
		assertThat(notes).hasSize(4);

		List<Long> ids = notes.stream().map(NoteDto::id).toList();
		assertThat(ids).containsExactlyInAnyOrder(1L, 2L, 3L, 8L);

		List<String> titles = notes.stream().map(NoteDto::title).toList();
		assertThat(titles).containsExactlyInAnyOrder(
				"Grocery List",
				"Buy groceries",
				"Gym workout",
				"Test note for settings");
	}

	@Test
	void shouldReturnNotesWithPagination() {

		ResponseEntity<List<NoteDto>> response = restTemplate
				.withBasicAuth("sophia", "Zaqwsx")
				.exchange(
						"/notes?page=0&size=2",
						HttpMethod.GET,
						null,
						new ParameterizedTypeReference<List<NoteDto>>() {
						});
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		List<NoteDto> notes = response.getBody();
		assertThat(notes).isNotNull();
		assertThat(notes).hasSize(2);

	}

	@Test
	void shouldReturnNotesWithSorting() {
		ResponseEntity<List<NoteDto>> response = restTemplate
				.withBasicAuth("sophia", "Zaqwsx")
				.exchange(
						"/notes?page=0&size=4&sort=title,desc",
						HttpMethod.GET,
						null,
						new ParameterizedTypeReference<List<NoteDto>>() {
						});
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		List<NoteDto> notes = response.getBody();
		assertThat(notes).isNotNull();
		assertThat(notes).hasSize(4);

		// Descending by title: "Test note for settings", "Gym workout", "Grocery List",
		// "Buy groceries"
		assertThat(notes.get(0).title()).isEqualTo("Test note for settings");
		assertThat(notes.get(1).title()).isEqualTo("Gym workout");
		assertThat(notes.get(2).title()).isEqualTo("Grocery List");
		assertThat(notes.get(3).title()).isEqualTo("Buy groceries");
	}

	@Test
	@DirtiesContext
	void shouldUpdateANote() {
		NoteDto updatedNote = new NoteDto(
				1L,
				"Updated Grocery List",
				"Updated list of groceries to buy",
				"text",
				null,
				false,
				false,
				"#00FF00",
				"sophia");

		HttpEntity<NoteDto> requestEntity = new HttpEntity<>(updatedNote);
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("sophia", "Zaqwsx")
				.exchange("/notes/1", HttpMethod.PUT, requestEntity, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<NoteDto> getResponse = restTemplate
				.withBasicAuth("sophia", "Zaqwsx")
				.getForEntity("/notes/1", NoteDto.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(getResponse.getBody()).isNotNull();
		assertThat(getResponse.getBody().title()).isEqualTo("Updated Grocery List");
		assertThat(getResponse.getBody().content()).isEqualTo("Updated list of groceries to buy");

	}

	@Test
	void shouldReturnNotFoundWhenUpdatingNonExistentNote() {
		NoteDto unknowCard = new NoteDto(
				999L,
				"Non-existent Note",
				"This note does not exist",
				"text",
				null,
				false,
				false,
				"#FF0000",
				"alejandro");
		HttpEntity<NoteDto> requestEntity = new HttpEntity<>(unknowCard);
		ResponseEntity<ErrorResponse> response = restTemplate
				.withBasicAuth("sophia", "Zaqwsx")
				.exchange("/notes/999", HttpMethod.PUT, requestEntity, ErrorResponse.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().message()).isEqualTo("Resource with ID 999 not found.");
		assertThat(response.getBody().statusCode()).isEqualTo(404);
	}

	@Test
	void shouldNotAllowUpdatingANoteTheyDoNotOwn() {
		NoteDto updatedNote = new NoteDto(
				4L,
				"Updated Note",
				"Trying to update a note not owned",
				"text",
				null,
				false,
				false,
				"#00FF00",
				null);

		HttpEntity<NoteDto> requestEntity = new HttpEntity<>(updatedNote);
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("sophia", "Zaqwsx")
				.exchange("/notes/4", HttpMethod.PUT, requestEntity, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isNull();
	}

	@Test
	@DirtiesContext
	void shouldDeleteANote() {
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("sophia", "Zaqwsx")
				.exchange("/notes/1", HttpMethod.DELETE, null, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<ErrorResponse> getResponse = restTemplate
				.withBasicAuth("sophia", "Zaqwsx")
				.getForEntity("/notes/1", ErrorResponse.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(getResponse.getBody()).isNotNull();
		assertThat(getResponse.getBody().message()).isEqualTo("Resource with ID 1 not found.");
	}

	@Test
	void shouldReturnNotFoundWhenDeletingNonExistentNote() {
		ResponseEntity<ErrorResponse> response = restTemplate
				.withBasicAuth("sophia", "Zaqwsx")
				.exchange("/notes/999", HttpMethod.DELETE, null, ErrorResponse.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().message()).isEqualTo("Resource with ID 999 not found.");
	}

	@Test
	void contextLoads() {
	}

	@Test
	void shouldNotAllowAccessToNotesTheyDoNotOwn() {
		ResponseEntity<ErrorResponse> response = restTemplate
				.withBasicAuth("sophia", "Zaqwsx")
				.getForEntity("/notes/4", ErrorResponse.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().message()).isEqualTo("Resource with ID 4 not found.");
	}

	@Test
	void shouldNotAllowDeletingNotesTheyDoNotOwn() {
		ResponseEntity<ErrorResponse> response = restTemplate
				.withBasicAuth("sophia", "Zaqwsx")
				.exchange("/notes/4", HttpMethod.DELETE, null, ErrorResponse.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().message()).isEqualTo("Resource with ID 4 not found.");
	}

}
