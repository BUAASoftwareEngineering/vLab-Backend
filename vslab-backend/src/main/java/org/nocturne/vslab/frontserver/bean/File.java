package org.nocturne.vslab.frontserver.bean;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class File {
    public enum FileType implements Serializable {
        FILE,
        DIR
    }

    private FileType fileType;

    private String fileName;

    private List<File> childFiles;
}