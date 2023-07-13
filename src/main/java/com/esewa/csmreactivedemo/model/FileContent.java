package com.esewa.csmreactivedemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "file_content")
public class FileContent {
    @Id
    private String id;
    private String fileName;
    private String actualData;
}
