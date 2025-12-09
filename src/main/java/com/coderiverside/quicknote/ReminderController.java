package com.coderiverside.quicknote;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/notes/{noteId}/reminders")
public class ReminderController {

        private final ReminderService reminderService;

        public ReminderController(ReminderService reminderService) {
                this.reminderService = reminderService;
        }

        @GetMapping("")
        public ResponseEntity<List<ReminderDto>> getAllReminders(
                        @PathVariable Long noteId,
                        Principal principal,
                        Pageable pageable) {
                List<ReminderDto> reminders = reminderService.getAllReminders(
                                noteId,
                                principal.getName(),
                                pageable);
                return ResponseEntity.ok(reminders);
        }

        @GetMapping("/{reminderId}")
        public ResponseEntity<ReminderDto> getReminderById(
                        @PathVariable Long noteId,
                        @PathVariable Long reminderId,
                        Principal principal) {
                ReminderDto reminder = reminderService.getReminderById(
                                noteId,
                                reminderId,
                                principal.getName());
                return ResponseEntity.ok(reminder);
        }

        @PostMapping("")
        public ResponseEntity<ReminderDto> create(
                        @PathVariable Long noteId,
                        @Valid @RequestBody ReminderDto reminderDto,
                        UriComponentsBuilder ucb,
                        Principal principal) {
                ReminderDto saved = reminderService.create(
                                noteId,
                                reminderDto,
                                principal.getName());
                URI location = ucb
                                .path("/notes/{noteId}/reminders/{reminderId}")
                                .buildAndExpand(noteId, saved.id())
                                .toUri();
                return ResponseEntity.created(location).body(saved);
        }

        @PutMapping("/{reminderId}")
        public ResponseEntity<ReminderDto> update(
                        @PathVariable Long noteId,
                        @PathVariable Long reminderId,
                        @Valid @RequestBody ReminderDto reminderDto,
                        Principal principal) {
                reminderService.update(
                                noteId,
                                reminderId,
                                reminderDto,
                                principal.getName());
                return ResponseEntity.noContent().build();
        }

        @DeleteMapping("/{reminderId}")
        public ResponseEntity<Void> delete(
                        @PathVariable Long noteId,
                        @PathVariable Long reminderId,
                        Principal principal) {
                reminderService.delete(
                                noteId,
                                reminderId,
                                principal.getName());
                return ResponseEntity.noContent().build();
        }

}
