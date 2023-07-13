package com.esewa.csmreactivedemo.service.minio;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

public interface MinioService {
    void uploadFile(FilePart file);
}
