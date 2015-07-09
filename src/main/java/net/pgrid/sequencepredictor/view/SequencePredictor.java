package net.pgrid.sequencepredictor.view;

import javax.swing.SwingUtilities;

/**
 * Main class of the Sequence Predictor application.
 *
 * @author Patrick Kramer
 */
public class SequencePredictor {

    /**
     * Entry point of the application.
     *
     * @param args The command-line arguments, ignored here.
     */
    public static void main(String[] args) {
        // By wrapping the call in `SwingUtilities.invokeLater`, we assert that
        // AWT/Swing is properly initialized before we attempt to set up a 
        // window.
        SwingUtilities.invokeLater(SequencePredictor::startApplication);
    }

    /**
     * Starts the application.
     */
    public static void startApplication() {
        LookAndFeelManager.setNativeLookAndFeel();
        MainWindow window = new MainWindow();
        window.init();
    }
}
