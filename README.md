# ScreenshotSearcher

ScreenshotSearcher is a Java application that allows you to search for text within image files (screenshots) using Optical Character Recognition (OCR). It utilizes the Tesseract OCR engine to extract text from images and provides a user interface to search through them.

## Prerequisites

- **Java Development Kit (JDK) 17** or higher
- **Maven** (for building and dependency management)

## Project Structure

- `src/main/java`: Source code
- `tessdata`: Directory containing Tesseract language data files (must be present for OCR to work)
- `screenshots`: Directory to store screenshots (optional, but useful for testing)

## Installation and Setup

1.  **Clone the repository** (if applicable) or download the source code.
2.  **Ensure `tessdata` is present**: The application requires the `tessdata` folder with language files (e.g., `eng.traineddata`) to be in the project root or a subdirectory named `ScreenshotSearcher/tessdata`.
3.  **Build the project**:
    Open a terminal in the project root directory and run:
    ```bash
    mvn clean install
    ```

## How to Run

You can run the application using Maven or by executing the built JAR file.

### Using Maven

Run the following command in the project root:

```bash
mvn exec:java -Dexec.mainClass="com.collegecode.Main"
```

### Running the JAR

After building the project (using `mvn clean install`), a JAR file will be created in the `target` directory. You can run it using:

```bash
java -jar target/ScreenshotSearcher-1.0-SNAPSHOT.jar
```

## Usage

1.  Launch the application.
2.  Use the interface to select a folder containing images/screenshots.
3.  The application will process the images and index the text.
4.  Enter keywords in the search bar to find images containing that text.

## Dependencies

- **Tess4J**: Java wrapper for Tesseract OCR API.
- **FlatLaf**: Flat Look and Feel for Java Swing applications (Dark Theme).
- **JUnit**: For unit testing.
