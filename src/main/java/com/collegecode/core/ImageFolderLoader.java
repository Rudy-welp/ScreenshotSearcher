package com.collegecode.core;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ImageFolderLoader {

    private static final String[] EXTENSIONS = new String[] { "png", "jpg", "jpeg", "bmp" };

    public List<File> loadImages(File directory) {
        if (directory == null || !directory.isDirectory()) {
            return Collections.emptyList();
        }

        FilenameFilter imageFilter = (dir, name) -> {
            for (String ext : EXTENSIONS) {
                if (name.toLowerCase().endsWith("." + ext)) {
                    return true;
                }
            }
            return false;
        };

        File[] files = directory.listFiles(imageFilter);
        return files != null ? Arrays.asList(files) : Collections.emptyList();
    }
}
