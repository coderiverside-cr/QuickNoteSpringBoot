package com.coderiverside.quicknote;

public record LabelDto(
        Long id,
        String name,
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
