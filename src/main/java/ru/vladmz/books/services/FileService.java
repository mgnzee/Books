package ru.vladmz.books.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.vladmz.books.etc.StorageDirectory;
import ru.vladmz.books.exceptions.FileStorageException;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.UUID;

@Service
public class FileService {

    private final StorageService storageService;

    public FileService(StorageService storageService) {
        this.storageService = storageService;
    }

    public String uploadPicture(Integer id, StorageDirectory directory, MultipartFile file){
        String fileName = generateFileName(directory, id, file.getOriginalFilename());
        try {
            storageService.upload(file.getInputStream(), directory.getBucket(), fileName, file.getContentType());
            return fileName;
        } catch (IOException | S3Exception ex) {
            throw new FileStorageException("Could not upload image to " + directory.getPath(), directory.getPath() ,fileName, ex);
        }
    }

    private String generateFileName(StorageDirectory directory, Integer id, String originalName){
        return directory.getPath() + "/" + id + "_" + UUID.randomUUID() + "_" + originalName;
    }

    public void deletePicture(String filename, StorageDirectory directory){
        if (filename != null) storageService.delete(filename, directory.getBucket());
    }
}
