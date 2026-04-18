package ru.vladmz.books.etc.pageSorting;

public enum CommentSort implements EntitySort{
    TIME("createdAt"),
    UPVOTES("upvotes");

    private final String fieldName;

    CommentSort(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }
}
