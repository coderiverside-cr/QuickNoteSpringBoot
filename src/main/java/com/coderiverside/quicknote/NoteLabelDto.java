package com.coderiverside.quicknote;

public record NoteLabelDto(
        Long labelId) {
    public static NoteLabelDto of(Long labelId) {
        return new NoteLabelDto(labelId);
    }
}
