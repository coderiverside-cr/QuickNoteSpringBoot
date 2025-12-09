package com.coderiverside.quicknote;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LabelDto(
        Long id,
        @NotBlank(message = "Label name is required") @Size(min = 1, max = 100, message = "Label name must be between 1 and 100 characters") String name,
        String owner) {
    public static LabelDto fromEntity(Label label) {
        return new LabelDto(
                label.getId(),
                label.getName(),
                label.getOwner());
    }

    public Label toEntity(String owner) {
        Label label = new Label();
        label.setName(this.name);
        label.setOwner(owner);
        return label;
    }

}
