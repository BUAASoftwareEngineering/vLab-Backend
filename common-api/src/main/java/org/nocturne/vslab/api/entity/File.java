package org.nocturne.vslab.api.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class File {
    public enum FileType {
        FILE,
        DIR
    }

    private FileType fileType;

    private String fileName;

    private List<File> childFiles;
}