package com.coderiverside.quicknote;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

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
@RequestMapping("/labels")
public class LabelController {

    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @GetMapping("")
    public ResponseEntity<List<LabelDto>> getAllLabels(
            Principal principal, Pageable pageable) {
        List<LabelDto> labels = labelService.getAllLabels(pageable, principal.getName());
        return ResponseEntity.ok(labels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabelDto> getLabelById(
            @PathVariable Long id,
            Principal principal) {
        LabelDto label = labelService.getLabelByIdDto(id, principal.getName());
        return ResponseEntity.ok(label);
    }

    @PostMapping("")
    public ResponseEntity<Void> create(
            @Valid @RequestBody LabelDto labelDto,
            UriComponentsBuilder ucb,
            Principal principal) {
        LabelDto created = labelService.createLabel(labelDto, principal.getName());
        URI location = ucb.path("/labels/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @Valid @RequestBody LabelDto labelDto,
            Principal principal) {
        labelService.updateLabel(id, labelDto, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Principal principal) {
        labelService.deleteLabel(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
