package ru.vladmz.books.etc;

public enum CommentSort {
    TIME("createdAt"),
    UPVOTES("upvotes");

    private final String fieldName;

    CommentSort(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
