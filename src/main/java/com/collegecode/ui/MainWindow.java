package com.collegecode.ui;

import com.collegecode.core.ProjectManager;
import com.collegecode.core.SearchService;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

public class MainWindow extends JFrame {

    private ProjectManager projectManager;
    private SearchService searchService; // Used for local highlighting

    private JList<File> fileList;
    private DefaultListModel<File> listModel;
    private JLabel imageLabel;
    private JTextArea ocrTextArea;
    private JTextField searchField;
    private JButton openFolderButton;
    private JButton searchButton;

    private List<File> currentFiles; // All files in folder

    public MainWindow() {
        super("Screenshot Searcher");
        this.projectManager = new ProjectManager();
        this.searchService = new SearchService();

        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLayout(new BorderLayout());

        // Top Panel: Controls
        JPanel topPanel = new JPanel();
        openFolderButton = new JButton("Open Folder");
        searchField = new JTextField(20);
        searchButton = new JButton("Search");

        openFolderButton.addActionListener(this::handleOpenFolder);
        searchButton.addActionListener(this::handleSearch);

        topPanel.add(openFolderButton);
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);

        add(topPanel, BorderLayout.NORTH);

        // Main Split Pane: File List | Content
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // Left: File List
        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleFileSelection(fileList.getSelectedValue());
            }
        });
        JScrollPane listScrollPane = new JScrollPane(fileList);
        listScrollPane.setPreferredSize(new Dimension(250, 0));
        mainSplit.setLeftComponent(listScrollPane);

        // Right: Content (Image | Text)
        JSplitPane contentSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // Image Preview
        imageLabel = new JLabel("No image selected", SwingConstants.CENTER);
        JScrollPane imageScrollPane = new JScrollPane(imageLabel);
        contentSplit.setLeftComponent(imageScrollPane);

        // OCR Text
        ocrTextArea = new JTextArea();
        ocrTextArea.setEditable(false);
        ocrTextArea.setLineWrap(true);
        ocrTextArea.setWrapStyleWord(true);
        JScrollPane textScrollPane = new JScrollPane(ocrTextArea);
        contentSplit.setRightComponent(textScrollPane);

        contentSplit.setDividerLocation(500);
        mainSplit.setRightComponent(contentSplit);

        add(mainSplit, BorderLayout.CENTER);
    }

    private void handleOpenFolder(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File folder = chooser.getSelectedFile();
            currentFiles = projectManager.loadFolder(folder);
            updateFileList(currentFiles);
        }
    }

    private void updateFileList(List<File> files) {
        listModel.clear();
        for (File file : files) {
            listModel.addElement(file);
        }
    }

    private void handleFileSelection(File file) {
        if (file == null)
            return;

        // Display Image
        ImageIcon icon = new ImageIcon(file.getAbsolutePath());
        Image img = icon.getImage();
        // Simple scaling logic
        int width = Math.min(img.getWidth(null), 600);
        int height = (int) (((double) width / img.getWidth(null)) * img.getHeight(null));
        Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(scaled));
        imageLabel.setText("");

        // Process OCR
        ocrTextArea.setText("Processing OCR...");
        projectManager.processImage(file, text -> {
            SwingUtilities.invokeLater(() -> {
                // Check if selection hasn't changed
                if (file.equals(fileList.getSelectedValue())) {
                    ocrTextArea.setText(text);
                    highlightText(searchField.getText());
                }
            });
        });
    }

    private void handleSearch(ActionEvent e) {
        String query = searchField.getText();
        if (currentFiles == null)
            return;

        if (query.isEmpty()) {
            updateFileList(currentFiles);
            return;
        }

        // Prepare UI for search
        searchButton.setEnabled(false);
        openFolderButton.setEnabled(false);
        listModel.clear();
        imageLabel.setIcon(null);
        imageLabel.setText("Searching...");
        ocrTextArea.setText("");

        // Perform async search
        projectManager.searchAll(currentFiles, query,
                // On Match
                file -> SwingUtilities.invokeLater(() -> {
                    boolean firstMatch = listModel.isEmpty();
                    listModel.addElement(file);
                    if (firstMatch) {
                        fileList.setSelectedValue(file, true);
                    }
                }),
                // On Complete
                () -> SwingUtilities.invokeLater(() -> {
                    searchButton.setEnabled(true);
                    openFolderButton.setEnabled(true);
                    if (listModel.isEmpty()) {
                        imageLabel.setText("No matches found.");
                    } else if ("Searching...".equals(imageLabel.getText())) {
                        // If we have matches but for some reason nothing is selected (shouldn't happen
                        // with logic above)
                        // clear the "Searching..." text
                        if (fileList.getSelectedValue() == null) {
                            imageLabel.setText("");
                        }
                    }
                }));
    }

    private void highlightText(String query) {
        ocrTextArea.getHighlighter().removeAllHighlights();
        String text = ocrTextArea.getText();
        if (query.isEmpty() || text.isEmpty())
            return;

        List<Integer> indices = searchService.search(text, query);
        Highlighter highlighter = ocrTextArea.getHighlighter();
        Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);

        for (int index : indices) {
            try {
                highlighter.addHighlight(index, index + query.length(), painter);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
