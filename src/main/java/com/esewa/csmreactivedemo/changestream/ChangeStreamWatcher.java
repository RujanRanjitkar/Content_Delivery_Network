package com.esewa.csmreactivedemo.changestream;

import com.esewa.csmreactivedemo.model.FileContent;
import com.esewa.csmreactivedemo.service.minio.MinioService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Component
@Slf4j
public class ChangeStreamWatcher {

    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final MinioService minioService;

    public ChangeStreamWatcher(ReactiveMongoTemplate reactiveMongoTemplate, MinioService minioService) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.minioService = minioService;
    }

    public void watchDatabaseChanges() {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("operationType").is("update"))
        );

        ChangeStreamOptions options = ChangeStreamOptions.builder()
                .filter(aggregation)
                .build();

        reactiveMongoTemplate.changeStream("file_content", options, FileContent.class)
                .doOnNext(changeEvent -> {
                    try {
                        processChange(Objects.requireNonNull(changeEvent.getBody()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .subscribe();
    }


    public FilePart createFilePart(String filename, String fileData) {
        return new FilePart() {
            @NotNull
            @Override
            public String filename() {
                return filename;
            }

            @NotNull
            @Override
            public Mono<Void> transferTo(@NotNull Path dest) {
                return Mono.fromCallable(() -> {
                            Files.writeString(dest, fileData);
                            return null; // Returning null since the write operation doesn't produce a useful result
                        })
                        .subscribeOn(Schedulers.boundedElastic())
                        .then(); // Executing the operation on a separate thread
            }

            @NotNull
            @Override
            public String name() {
                return "file";
            }

            @NotNull
            @Override
            public HttpHeaders headers() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("file", filename);
                return headers;
            }

            @NotNull
            @Override
            public Flux<DataBuffer> content() {
                byte[] fileBytes = fileData.getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = new DefaultDataBufferFactory().wrap(fileBytes);
                return Flux.just(buffer);
            }
        };
    }


    private void processChange(FileContent changedDocument) throws IOException {
        log.info("Upload file after change in db ");

        FilePart part = createFilePart(changedDocument.getFileName(), changedDocument.getActualData());
        minioService.uploadFile(part);
    }

}





