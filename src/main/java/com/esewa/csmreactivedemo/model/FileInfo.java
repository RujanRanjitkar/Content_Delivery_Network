package com.esewa.csmreactivedemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "file_info")
public class FileInfo {
    @Id
    private String id;
    private String name;
    private String path;
    private String size;
    private String contentType;
}
