package org.nocturne.vslab.frontserver.bean;

import java.io.Serializable;

public enum ImageType implements Serializable {
    PYTHON2,
    PYTHON3,
    CPP,
    JAVA;

    public String getImageName() {
        switch (this) {
            case PYTHON3: return "vlab-python";
            case PYTHON2: return "vlab-python";
            case JAVA: return "vlab-base";
            case CPP: return "vlab-cpp";
            default: return "vlab-base";
        }
    }
}
