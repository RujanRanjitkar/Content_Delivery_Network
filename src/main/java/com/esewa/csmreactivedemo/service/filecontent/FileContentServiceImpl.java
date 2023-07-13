package com.esewa.csmreactivedemo.service.filecontent;

import com.esewa.csmreactivedemo.model.FileContent;
import com.esewa.csmreactivedemo.repo.FileContentRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileContentServiceImpl implements FileContentService {

    private final FileContentRepo fileContentRepo;
    @Override
    public void saveFileContent(String jsonData) {
        FileContent fileContent = new FileContent();
        fileContent.setActualData(jsonData);
        fileContent.setFileName("dummy");

        log.info("saving file content");
        //saving file content using reactive
        Mono<FileContent> saveFileContentMono = fileContentRepo.save(fileContent);

        saveFileContentMono.subscribe();
    }
}
