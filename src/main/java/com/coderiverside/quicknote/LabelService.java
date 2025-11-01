package com.coderiverside.quicknote;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coderiverside.quicknote.exception.BadRequestException;
import com.coderiverside.quicknote.exception.ResourceNotFoundException;

@Service
public class LabelService {

    private final LabelRepository labelRepository;
    private final NoteLabelRepository noteLabelRepository;

    public LabelService(LabelRepository labelRepository, NoteLabelRepository noteLabelRepository) {
        this.labelRepository = labelRepository;
        this.noteLabelRepository = noteLabelRepository;
    }

    // Internal method for other services
    protected Label getLabelById(Long id, String owner) {
        if (id == null) {
            throw new BadRequestException("Label id cannot be null");
        }
        return labelRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    // Public API methods now work with DTOs
    public List<LabelDto> getAllLabels(Pageable pageable, String owner) {
        if (pageable == null) {
            throw new BadRequestException("Pageable cannot be null");
        }
        return labelRepository.findAllByOwner(
                owner,
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSort()))
                .getContent()
                .stream()
                .map(LabelDto::fromEntity)
                .toList();
    }

    public LabelDto getLabelByIdDto(Long id, String owner) {
        return LabelDto.fromEntity(getLabelById(id, owner));
    }

    @Transactional
    public LabelDto createLabel(LabelDto labelDto, String owner) {
        validateLabelDto(labelDto);

        // Check for duplicate names
        labelRepository.findByNameAndOwner(labelDto.name(), owner).ifPresent(l -> {
            throw new BadRequestException("Label name already exists");
        });

        Label label = labelDto.toEntity(owner);
        Label saved = labelRepository.save(label);
        return LabelDto.fromEntity(saved);
    }

    @Transactional
    public LabelDto updateLabel(Long id, LabelDto labelDto, String owner) {
        validateLabelDto(labelDto);
        if (id == null) {
            throw new BadRequestException("Label id cannot be null");
        }

        Label existingLabel = getLabelById(id, owner);

        // Check for duplicate names but exclude current label
        labelRepository.findByNameAndOwner(labelDto.name(), owner)
                .ifPresent(l -> {
                    if (!id.equals(l.getId())) {
                        throw new BadRequestException("Label name already exists");
                    }
                });

        existingLabel.setName(labelDto.name());
        Label updated = labelRepository.save(existingLabel);
        return LabelDto.fromEntity(updated);
    }

    @Transactional
    // El método deleteLabel(Long labelId) fue reemplazado por deleteLabel(Long
    // labelId, String owner)
    public void deleteLabel(Long labelId, String owner) {
        // Borrar asociaciones en NoteLabel
        List<NoteLabel> noteLabels = noteLabelRepository.findAll();
        noteLabels.stream()
                .filter(nl -> nl.getId().getLabelId().equals(labelId))
                .forEach(noteLabelRepository::delete);
        // Borrar el label
        Label label = getLabelById(labelId, owner);
        labelRepository.delete(label);
    }

    // Helper methods
    private void validateLabelDto(LabelDto labelDto) {
        if (labelDto == null) {
            throw new BadRequestException("Label cannot be null");
        }
        if (labelDto.name() == null || labelDto.name().trim().isEmpty()) {
            throw new BadRequestException("Label name cannot be empty");
        }
    }
}
