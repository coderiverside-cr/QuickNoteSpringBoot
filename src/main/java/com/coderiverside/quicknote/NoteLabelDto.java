package com.coderiverside.quicknote;

import jakarta.validation.constraints.NotNull;

public record NoteLabelDto(
        @NotNull(message = "Label ID is required") Long labelId) {
    public static NoteLabelDto of(Long labelId) {
        return new NoteLabelDto(labelId);
    }
}
