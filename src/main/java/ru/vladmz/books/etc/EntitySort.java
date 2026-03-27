package ru.vladmz.books.etc;

public enum EntitySort {
    TIME("createdAt"),
    UPVOTES("upvotes");

    private final String fieldName;

    EntitySort(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
