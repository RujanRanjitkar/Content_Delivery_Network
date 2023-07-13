package com.esewa.csmreactivedemo.changestream;

import com.esewa.csmreactivedemo.model.FileContent;
import com.esewa.csmreactivedemo.service.minio.MinioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

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
                .doOnNext(changeEvent -> processChange(Objects.requireNonNull(changeEvent.getBody())))
                .subscribe();
    }

    private void processChange(FileContent changedDocument) {

        System.out.println(changedDocument.getActualData());
        System.out.println(changedDocument.getFileName());
        // Call the uploadFile method of MinioService with the changed document
        log.info("Upload file after change in db");

        // Perform the conversion from FileContent to FilePart
//        FilePart filePart = convertToFilePart(changedDocument);
//        minioService.uploadFile(filePart);
    }

}





