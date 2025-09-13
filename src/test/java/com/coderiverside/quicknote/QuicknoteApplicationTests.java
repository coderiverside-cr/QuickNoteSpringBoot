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
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("sophia", "Zaqwsx")
				.getForEntity("/notes/25", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isBlank();

	}

	@Test
	@DirtiesContext
	void shouldCreateANote() {
		NoteDto newNote = new NoteDto(
				0L,
				"Meeting Notes",
				"Discuss project roadmap and milestones",
				"text",
				null,
				false,
				false,
				"blue",
				"sophia");
		ResponseEntity<Void> createResponse = restTemplate
				.withBasicAuth("sophia", "Zaqwsx")
				.postForEntity("/notes", newNote, Void.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		URI locationOfNote = createResponse.getHeaders().getLocation();
		ResponseEntity<String> getResponse = restTemplate
				.withBasicAuth("sophia", "Zaqwsx")
				.getForEntity(locationOfNote, String.class);
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
		assertThat(notes).hasSize(3);

		List<Long> ids = notes.stream().map(NoteDto::id).toList();
		assertThat(ids).containsExactlyInAnyOrder(1L, 2L, 3L);

		List<String> titles = notes.stream().map(NoteDto::title).toList();
		assertThat(titles).containsExactlyInAnyOrder(
				"Grocery List",
				"Buy groceries",
				"Gym workout");

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
		List<NoteDto> notes = response.getBody();
		assertThat(notes).isNotNull();
		assertThat(notes).hasSize(2);

	}

	@Test
	void shouldReturnNotesWithSorting() {
		ResponseEntity<List<NoteDto>> response = restTemplate
				.withBasicAuth("sophia", "Zaqwsx")
				.exchange(
						"/notes?page=0&size=3&sort=title,desc",
						HttpMethod.GET,
						null,
						new ParameterizedTypeReference<List<NoteDto>>() {
						});

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		List<NoteDto> notes = response.getBody();
		assertThat(notes).isNotNull();
		assertThat(notes).hasSize(3);

		String lastTitle = notes.get(0).title();
		assertThat(lastTitle).isEqualTo("Gym workout");

		String firstTitle = notes.get(2).title();
		assertThat(firstTitle).isEqualTo("Buy groceries");
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
				"green",
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
				"red",
				"alejandro");
		HttpEntity<NoteDto> requestEntity = new HttpEntity<>(unknowCard);
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("sophia", "Zaqwsx")
				.exchange("/notes/999", HttpMethod.PUT, requestEntity, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isNull();
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
				"green",
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

		ResponseEntity<NoteDto> getResponse = restTemplate
				.withBasicAuth("sophia", "Zaqwsx")
				.getForEntity("/notes/1", NoteDto.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldReturnNotFoundWhenDeletingNonExistentNote() {
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("sophia", "Zaqwsx")
				.exchange("/notes/999", HttpMethod.DELETE, null, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void contextLoads() {
	}

	@Test
	void shouldNotAllowAccessToNotesTheyDoNotOwn() {
		ResponseEntity<NoteDto> response = restTemplate
				.withBasicAuth("sophia", "Zaqwsx")
				.getForEntity("/notes/4", NoteDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldNotAllowDeletingNotesTheyDoNotOwn() {
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("sophia", "Zaqwsx")
				.exchange("/notes/4", HttpMethod.DELETE, null, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

}
