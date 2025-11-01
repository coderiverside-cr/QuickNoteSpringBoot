package com.coderiverside.quicknote;

import java.net.URI;
import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notes/{noteId}/labels")
public class NoteLabelController {

    private final NoteLabelService noteLabelService;

    public NoteLabelController(NoteLabelService noteLabelService) {
        this.noteLabelService = noteLabelService;
    }

    @GetMapping("")
    public ResponseEntity<List<LabelDto>> list(
            @PathVariable Long noteId,
            Principal principal) {
        List<LabelDto> labels = noteLabelService.listLabelsForNote(
                noteId,
                principal.getName());
        return ResponseEntity.ok(labels);
    }

    @PostMapping("")
    public ResponseEntity<Void> add(
            @PathVariable Long noteId,
            @RequestBody NoteLabelDto noteLabelDto,
            Principal principal) {
        noteLabelService.addLabelToNote(noteId, noteLabelDto.labelId(), principal.getName());
        URI location = URI.create(String.format("/notes/%d/labels/%d", noteId, noteLabelDto.labelId()));
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/{labelId}")
    public ResponseEntity<Void> remove(@PathVariable Long noteId, @PathVariable Long labelId, Principal principal) {
        noteLabelService.removeLabelFromNote(noteId, labelId, principal.getName());
        return ResponseEntity.noContent().build();
    }

}
