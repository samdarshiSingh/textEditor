import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.regex.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;

public class BestTextEditor extends JFrame implements ActionListener {

    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JFileChooser fileChooser;
    private UndoManager undoManager;

    public BestTextEditor() {
        super("Best Text Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        // Enable word wrap
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        // Add line numbers
        JTextArea lineNumberArea = new JTextArea("1");
        lineNumberArea.setBackground(Color.LIGHT_GRAY);
        lineNumberArea.setEditable(false);
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateLineNumbers();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateLineNumbers();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateLineNumbers();
            }

            private void updateLineNumbers() {
                SwingUtilities.invokeLater(() -> {
                    int totalLines = textArea.getLineCount();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i <= totalLines; i++) {
                        sb.append(i).append("\n");
                    }
                    lineNumberArea.setText(sb.toString());
                });
            }
        });

        scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(lineNumberArea);

        fileChooser = new JFileChooser();
        undoManager = new UndoManager();

        createMenuBar();
        createToolbar();

        // Register the undo manager for the text area to handle undo/redo operations
        textArea.getDocument().addUndoableEditListener(undoManager);

        add(scrollPane);
        setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem openMenuItem = new JMenuItem("Open");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        JMenuItem exitMenuItem = new JMenuItem("Exit");

        openMenuItem.addActionListener(this);
        saveMenuItem.addActionListener(this);
        exitMenuItem.addActionListener(this);

        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem cutMenuItem = new JMenuItem("Cut");
        JMenuItem copyMenuItem = new JMenuItem("Copy");
        JMenuItem pasteMenuItem = new JMenuItem("Paste");
        JMenuItem undoMenuItem = new JMenuItem("Undo");
        JMenuItem redoMenuItem = new JMenuItem("Redo");
        JMenuItem findMenuItem = new JMenuItem("Find/Replace");

        cutMenuItem.addActionListener(this);
        copyMenuItem.addActionListener(this);
        pasteMenuItem.addActionListener(this);
        undoMenuItem.addActionListener(this);
        redoMenuItem.addActionListener(this);
        findMenuItem.addActionListener(this);

        editMenu.add(cutMenuItem);
        editMenu.add(copyMenuItem);
        editMenu.add(pasteMenuItem);
        editMenu.addSeparator();
        editMenu.add(undoMenuItem);
        editMenu.add(redoMenuItem);
        editMenu.addSeparator();
        editMenu.add(findMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        setJMenuBar(menuBar);
    }

    private void createToolbar() {
        JToolBar toolBar = new JToolBar();

        // Create toolbar buttons with icons
        ImageIcon openIcon = new ImageIcon("open_icon.png");
        ImageIcon saveIcon = new ImageIcon("save_icon.png");
        ImageIcon cutIcon = new ImageIcon("cut_icon.png");
        ImageIcon copyIcon = new ImageIcon("copy_icon.png");
        ImageIcon pasteIcon = new ImageIcon("paste_icon.png");
        ImageIcon undoIcon = new ImageIcon("undo_icon.png");
        ImageIcon redoIcon = new ImageIcon("redo_icon.png");
        ImageIcon findIcon = new ImageIcon("find_icon.png");

        JButton openButton = new JButton(openIcon);
        JButton saveButton = new JButton(saveIcon);
        JButton cutButton = new JButton(cutIcon);
        JButton copyButton = new JButton(copyIcon);
        JButton pasteButton = new JButton(pasteIcon);
        JButton undoButton = new JButton(undoIcon);
        JButton redoButton = new JButton(redoIcon);
        JButton findButton = new JButton(findIcon);

        // Set tooltips for buttons
        openButton.setToolTipText("Open");
        saveButton.setToolTipText("Save");
        cutButton.setToolTipText("Cut");
        copyButton.setToolTipText("Copy");
        pasteButton.setToolTipText("Paste");
        undoButton.setToolTipText("Undo");
        redoButton.setToolTipText("Redo");
        findButton.setToolTipText("Find/Replace");

        // Add action listeners for buttons
        openButton.addActionListener(this);
        saveButton.addActionListener(this);
        cutButton.addActionListener(this);
        copyButton.addActionListener(this);
        pasteButton.addActionListener(this);
        undoButton.addActionListener(this);
        redoButton.addActionListener(this);
        findButton.addActionListener(this);

        // Add buttons to the toolbar
        toolBar.add(openButton);
        toolBar.add(saveButton);
        toolBar.addSeparator();
        toolBar.add(cutButton);
        toolBar.add(copyButton);
        toolBar.add(pasteButton);
        toolBar.addSeparator();
        toolBar.add(undoButton);
        toolBar.add(redoButton);
        toolBar.addSeparator();
        toolBar.add(findButton);

        add(toolBar, BorderLayout.PAGE_START);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "Open":
                openFile();
                break;
            case "Save":
                saveFile();
                break;
            case "Cut":
                textArea.cut();
                break;
            case "Copy":
                textArea.copy();
                break;
            case "Paste":
                textArea.paste();
                break;
            case "Undo":
                undo();
                break;
            case "Redo":
                redo();
                break;
            case "Find/Replace":
                showFindReplaceDialog();
                break;
        }
    }

    private void openFile() {
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                BufferedReader reader = new BufferedReader(new FileReader(file));
                textArea.read(reader, null);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFile() {
        int returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                textArea.write(writer);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void undo() {
        if (undoManager.canUndo()) {
            undoManager.undo();
        }
    }

    private void redo() {
        if (undoManager.canRedo()) {
            undoManager.redo();
        }
    }

    private void showFindReplaceDialog() {
        String findText = JOptionPane.showInputDialog(this, "Find:");
        if (findText != null && !findText.isEmpty()) {
            String text = textArea.getText();
            int start = text.indexOf(findText);
            if (start != -1) {
                textArea.setSelectionStart(start);
                textArea.setSelectionEnd(start + findText.length());
                textArea.requestFocusInWindow();
            } else {
                JOptionPane.showMessageDialog(this, "Text not found.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BestTextEditor());
    }
}
