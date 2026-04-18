package ru.vladmz.books.etc.pageSorting;

public enum BookSort implements EntitySort{
    TIME("createdAt");

    private final String fieldName;

    BookSort(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }
}
