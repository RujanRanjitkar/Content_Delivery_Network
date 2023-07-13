package com.esewa.csmreactivedemo.coltroller;

import com.esewa.csmreactivedemo.service.filecontent.FileContentService;
import com.esewa.csmreactivedemo.service.minio.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/minio-server")
@RequiredArgsConstructor
public class FileController {

    private final MinioService minioService;
    private final FileContentService fileContentService;

    @PostMapping("/upload")
    public Mono<String> uploadFile(FilePart file) throws IOException {

        File convFile = new File(file.filename());
        file.transferTo(convFile).subscribe();
        String actualData = Files.readString(convFile.toPath());

        fileContentService.saveFileContent(actualData, file.filename());

        minioService.uploadFile(file);
        return Mono.just("saved");
    }

}
