package com.coderiverside.quicknote;

public record NoteSettingsDto(
        Long id,
        boolean locked,
        String priority,
        boolean enableSharing) {

    public static NoteSettingsDto fromEntity(NoteSettings s) {        
        return new NoteSettingsDto(
                s.getId(),
                s.isLocked(),
                s.getPriority(),
                s.isEnableSharing());
    }

    public NoteSettings toEntity() {
        NoteSettings noteSettings = new NoteSettings();
        noteSettings.setLocked(this.locked);
        noteSettings.setPriority(this.priority != null ? this.priority : "LOW");
        noteSettings.setEnableSharing(this.enableSharing);
        return noteSettings;
    }

    public NoteSettings toEntity(Note note) {
        NoteSettings noteSettings = toEntity();
        noteSettings.setNote(note);
        return noteSettings;
    }
}
