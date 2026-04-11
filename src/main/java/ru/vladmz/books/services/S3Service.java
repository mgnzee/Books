package ru.vladmz.books.services;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;

@Service
public class S3Service implements StorageService{

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public S3Service(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    @Override
    public String upload(InputStream inputStream, String bucket, String path, String contentType) {
        try {
            long size = inputStream.available();

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(path)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(inputStream, size));

            return path;
        } catch (IOException ex){
            throw new RuntimeException("Couldn't read file input stream", ex);
        }
    }

    @Override
    public void delete(String path, String bucket) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(path)
                .build());
    }

    @Override
    public boolean exists(String path, String bucket) {
        try{
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(path)
                    .build());
            return true;
        } catch (S3Exception ex){
            return false;
        }
    }

    @Override
    public String getPresignedUrl(String path, String bucket) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(path)
                .build();
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .getObjectRequest(getObjectRequest)
                .build();
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    @Override
    public InputStream download(String path, String bucket) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(path)
                .build();
        return s3Client.getObject(getObjectRequest);
    }
}
