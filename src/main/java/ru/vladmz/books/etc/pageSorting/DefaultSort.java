package ru.vladmz.books.etc.pageSorting;

public enum DefaultSort implements EntitySort{
    TIME("createdAt");

    private final String fieldName;

    DefaultSort(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }
}
