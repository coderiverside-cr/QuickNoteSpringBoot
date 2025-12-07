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

    public NoteSettings toEntity(Note note) {
        NoteSettings noteSettings = new NoteSettings();
        noteSettings.setLocked(this.locked);
        noteSettings.setPriority(this.priority != null ? this.priority : "LOW");
        noteSettings.setEnableSharing(this.enableSharing);
        noteSettings.setNote(note);
        return noteSettings;
    }

}
