package com.example.demo.enums;

import lombok.Getter;

@Getter
public enum Status {
    CREATED(true, true),
    DELETED(false, false),
    VERIFIED(true, true),
    ACCEPTED(false, true),
    REJECTED(false, false),
    PUBLISHED(false, false);

    private final boolean editableBody;
    private final boolean editableStatus;

    Status(boolean isEditableBody, boolean isEditableStatus) {
        editableBody = isEditableBody;
        editableStatus = isEditableStatus;
    }

}
