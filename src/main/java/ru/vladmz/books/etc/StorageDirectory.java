package ru.vladmz.books.etc;

public enum StorageDirectory {
    //assets
    AVATAR("avatars", "public-assets"),
    BOOK_COVER("books", "public-assets"),
    BOOKSHELF_COVER("bookshelves", "public-assets"),
    //library
    BOOK_FILE("books", "library");

    private final String path;
    private final String bucket;

    StorageDirectory(String path, String bucket) {
        this.path = path;
        this.bucket = bucket;
    }

    public String getPath(){
        return path;
    }

    public String getBucket() {
        return bucket;
    }
}
