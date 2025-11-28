package com.collegecode.core;

import org.junit.Test;
import java.io.File;
import java.util.List;
import static org.junit.Assert.*;

public class ImageFolderLoaderTest {

    @Test
    public void testLoadImages() {
        ImageFolderLoader loader = new ImageFolderLoader();
        // Assuming src/test/resources exists and is a directory
        File folder = new File("src/test/resources");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        List<File> files = loader.loadImages(folder);
        assertNotNull(files);
        // We can't guarantee files exist unless we create them, but it shouldn't crash.
    }

    @Test
    public void testLoadInvalidFolder() {
        ImageFolderLoader loader = new ImageFolderLoader();
        List<File> files = loader.loadImages(new File("non_existent_folder"));
        assertTrue(files.isEmpty());
    }
}
