package net.pgrid.sequencepredictor.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.LinkedList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import net.pgrid.sequencepredictor.InputParser;
import net.pgrid.sequencepredictor.InvalidTokenException;
import net.pgrid.sequencepredictor.NoPatternFoundException;
import net.pgrid.sequencepredictor.Predictor;

/**
 * Creates and controls the main window for the application.
 * @author Patrick Kramer
 */
public class MainWindow {
    /**
     * The spacing along the left and right sides.
     */
    public static final int SIDE_SPACING = 5;
    
    /**
     * The spacing from the top.
     */
    public static final int TOP_SPACING = 3;
    
    /**
     * The spacing from the bottom.
     * <p>
     * This is set so that the spacing between lines is equal to 
     * {@code SIDE_SPACING} (taking the {@code TOP_SPACING} into account.
     * </p>
     */
    public static final int BOTTOM_SPACING = SIDE_SPACING - TOP_SPACING;
    
    /**
     * The amount of elements to show in the result box, and the amount of 
     * elements to add when clicking the "More" button.
     */
    public static final int RESULT_STEP_SIZE = 5;
    
    private final JFrame window;
    
    private final JTextField input = new JTextField();
    private final JTextArea output = new JTextArea();
    
    private Predictor predictor = null;
    
    public MainWindow() {
        this.window = new JFrame("Sequence Predictor");
    }
    
    public void init() {
        this.window.setLayout(new GridBagLayout());
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 1;
        gbc.insets = new Insets(TOP_SPACING, SIDE_SPACING, BOTTOM_SPACING, SIDE_SPACING);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        
        JLabel inputLabel = new JLabel("Input Sequence (comma-separated):");
        this.window.add(inputLabel, gbc);
        this.window.add(this.input, gbc);
        
        JButton computeButton = new JButton("Compute");
        computeButton.addActionListener(e -> compute());
        gbc.fill = GridBagConstraints.NONE;
        this.window.add(computeButton, gbc);
        
        output.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(output);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        this.window.add(scrollPane, gbc);
        
        JButton showMoreButton = new JButton("Show More");
        showMoreButton.addActionListener(e -> showMore());
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        this.window.add(showMoreButton, gbc);
        
        this.window.setMinimumSize(new Dimension(300, 200));
        this.window.pack();
        this.window.setLocationRelativeTo(null);
        this.window.setVisible(true);
    }
    
    public List<Double> getInputValues() {
        try {
            InputParser parser = new InputParser(input.getText());
            return parser.parse()
                .reduce(new LinkedList<>(), 
                        (l, d) -> {
                            l.add(d);
                            return l;
                        },
                        (a, b) -> {
                            a.addAll(b);
                            return a;
                        });
        } catch (InvalidTokenException ex) {
            // The message of a NumberFormatException contains the token that could not be parsed.
            output.setText("Error parsing input:\n" + ex.getMessage());
            return null;
        }
    }
    
    public void compute() {
        List<Double> inputValues = getInputValues();
        if (inputValues != null) {
            predictor = new Predictor(getInputValues());
            try { 
                predictor.init();
                output.setText("");
                showMore();
            } catch (NoPatternFoundException ex) {
                output.setText(ex.getMessage());
                predictor = null;
            }
        }
    }
    
    /**
     * Adds {@code RESULT_STEP_SIZE} elements to the output pane.
     */
    public void showMore() {
        if (predictor == null) {
            output.setText("No sequence computed or no pattern found in computed sequence.");
        } else {
            predictor.stream()
                    .limit(RESULT_STEP_SIZE)
                    .forEach(this::addValue);
            output.setText(output.getText() + "\n");
        }
    }
    
    /**
     * Adds a single value to the output field.
     * @param value The value to add.
     */
    public void addValue(double value) {
        String text = output.getText();
        if (!text.isEmpty() && !text.endsWith("\n")) {
            text += ", ";
        }
        
        output.setText(text + value);
    }
}
