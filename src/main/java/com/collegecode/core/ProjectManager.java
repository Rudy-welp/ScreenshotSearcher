package com.collegecode.core;

import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ProjectManager {

    private ImageFolderLoader folderLoader;
    private OCRService ocrService;
    private SearchService searchService;

    // Maps File -> OCR Text
    private Map<File, String> ocrResults;

    public ProjectManager() {
        this.folderLoader = new ImageFolderLoader();
        this.ocrService = new OCRService();
        this.searchService = new SearchService();
        this.ocrResults = new HashMap<>();
    }

    public List<File> loadFolder(File folder) {
        ocrResults.clear();
        return folderLoader.loadImages(folder);
    }

    public void processImage(File file, Consumer<String> onComplete) {
        if (ocrResults.containsKey(file)) {
            onComplete.accept(ocrResults.get(file));
            return;
        }

        CompletableFuture.supplyAsync(() -> {
            try {
                return ocrService.extractText(file);
            } catch (TesseractException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }).thenAccept(text -> {
            ocrResults.put(file, text);
            onComplete.accept(text);
        });
    }

    public boolean searchInFile(File file, String query) {
        String text = ocrResults.get(file);
        if (text == null)
            return false;
        return !searchService.search(text, query).isEmpty();
    }

    public void searchAll(List<File> files, String query, Consumer<File> onMatch, Runnable onComplete) {
        CompletableFuture.runAsync(() -> {
            for (File file : files) {
                String text;
                synchronized (ocrResults) {
                    text = ocrResults.get(file);
                }

                if (text == null) {
                    try {
                        text = ocrService.extractText(file);
                        synchronized (ocrResults) {
                            ocrResults.put(file, text);
                        }
                    } catch (TesseractException e) {
                        e.printStackTrace();
                        text = "";
                    }
                }

                if (!searchService.search(text, query).isEmpty()) {
                    onMatch.accept(file);
                }
            }
            onComplete.run();
        });
    }

    public String getCachedText(File file) {
        synchronized (ocrResults) {
            return ocrResults.get(file);
        }
    }
}
