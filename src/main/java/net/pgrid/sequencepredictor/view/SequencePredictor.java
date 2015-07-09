package net.pgrid.sequencepredictor.view;

import javax.swing.SwingUtilities;

/**
 * Main class of the Sequence Predictor application.
 * @author Patrick Kramer
 */
public class SequencePredictor {
    
    /**
     * Entry point of the application.
     * @param args The command-line arguments, ignored here.
     */
    public static void main(String[] args) {
        MainWindow window = new MainWindow();
        SwingUtilities.invokeLater(window::init);
    }
}
