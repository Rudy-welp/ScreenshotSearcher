package com.collegecode.core;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;
import java.io.File;

public class OCRService {

    private Tesseract tesseract;

    public OCRService() {
        this.tesseract = new Tesseract();

        // Try to find tessdata in current directory or project subdirectory
        File tessDataFolder = new File("tessdata");
        if (!tessDataFolder.exists()) {
            File altFolder = new File("ScreenshotSearcher/tessdata");
            if (altFolder.exists()) {
                tessDataFolder = altFolder;
            }
        }

        // Use absolute path to ensure it's found regardless of working directory
        this.tesseract.setDatapath(tessDataFolder.getAbsolutePath());
        this.tesseract.setLanguage("eng");

        // Debug print
        System.out.println("Current Working Directory: " + new File(".").getAbsolutePath());
        System.out.println("TessData Path: " + tessDataFolder.getAbsolutePath());
        if (!tessDataFolder.exists()) {
            System.err.println("WARNING: tessdata folder does not exist at: " + tessDataFolder.getAbsolutePath());
        }
    }

    public String extractText(BufferedImage image) throws TesseractException {
        return tesseract.doOCR(image);
    }

    public String extractText(File imageFile) throws TesseractException {
        return tesseract.doOCR(imageFile);
    }
}
