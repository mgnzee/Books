package ru.vladmz.books.services;

import org.springframework.stereotype.Service;
import ru.vladmz.books.DTOs.FileUploadRequest;
import ru.vladmz.books.etc.StorageDirectory;
import ru.vladmz.books.exceptions.FileStorageException;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.InputStream;
import java.util.UUID;

@Service
public class FileService {

    private final StorageService storageService;

    public FileService(StorageService storageService) {
        this.storageService = storageService;
    }

    public String uploadResource(Integer id, StorageDirectory directory, FileUploadRequest file){
        String fileName = generateFileName(directory, id, file.originalFileName());
        try {
            storageService.upload(file.inputStream(), directory.getBucket(), fileName, file.contentType());
            return fileName;
        } catch (S3Exception ex) {
            throw new FileStorageException("Could not upload image to " + directory.getPath(), directory.getPath() ,fileName, ex);
        }
    }

    private String generateFileName(StorageDirectory directory, Integer id, String originalName){
        return directory.getPath() + "/" + id + "_" + UUID.randomUUID() + "_" + originalName;
    }

    public void deleteResource(String filename, StorageDirectory directory){
        if (filename != null) storageService.delete(filename, directory.getBucket());
    }
}
