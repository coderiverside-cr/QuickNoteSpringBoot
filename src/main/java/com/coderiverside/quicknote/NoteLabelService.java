package com.coderiverside.quicknote;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coderiverside.quicknote.exception.BadRequestException;
import com.coderiverside.quicknote.exception.ResourceNotFoundException;

@Service
public class NoteLabelService {

    private final NoteLabelRepository noteLabelRepository;
    private final LabelRepository labelRepository;
    private final NoteService noteService;

    public NoteLabelService(NoteLabelRepository noteLabelRepository, LabelRepository labelRepository,
            NoteService noteService) {
        this.noteLabelRepository = noteLabelRepository;
        this.labelRepository = labelRepository;
        this.noteService = noteService;
    }

    @Transactional(readOnly = true)
    public List<LabelDto> listLabelsForNote(Long noteId, String owner) {
        noteService.getNoteById(noteId, owner);

        return noteLabelRepository.findAllByIdNoteId(noteId)
                .stream()
                .map(NoteLabel::getLabel)
                .map(label -> new LabelDto(label.getId(), label.getName(), label.getOwner()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addLabelToNote(Long noteId, Long labelId, String owner) {
        Note note = noteService.getNoteById(noteId, owner);
        Label label = getLabelById(labelId);

        NoteLabelId id = new NoteLabelId(noteId, labelId);

        if (noteLabelRepository.existsById(id)) {
            throw new BadRequestException("Label already associated to note");
        }

        NoteLabel nl = new NoteLabel(note, label);
        noteLabelRepository.save(nl);
    }

    protected Label getLabelById(Long labelId) {
        return labelRepository.findById(labelId)
                .orElseThrow(() -> new ResourceNotFoundException(labelId));
    }

    @Transactional
    public void removeLabelFromNote(Long noteId, Long labelId, String owner) {
        noteService.getNoteById(noteId, owner);
        getLabelById(labelId); 

        NoteLabelId id = new NoteLabelId(noteId, labelId);
        if (!noteLabelRepository.existsById(id)) {
            throw new BadRequestException("Label not associated to note");
        }
        
        noteLabelRepository.deleteById(id);
    }
}
