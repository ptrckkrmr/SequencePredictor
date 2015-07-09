package net.pgrid.sequencepredictor.view;

import java.util.Arrays;
import java.util.Locale;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Utility class for easier dealing with LookAndFeel instances.
 * 
 * This class enables quicker (and safer) setting of LookAndFeel, and allows 
 * setting the native LookAndFeel of the OS with a single method call (even on
 * Windows, where the default LaF is 'metal', and not 'windows').
 * 
 * @author Patrick Kramer
 */
public class LookAndFeelManager {
    
    /**
     * Private constructor prevents instantiation.
     */
    private LookAndFeelManager() {
    }
    
    /**
     * Sets the LookAndFeel to the LookAndFeel with the given name.
     * @param name The name of the desired LookAndFeel, not null.
     * @throws     IllegalArgumentException - If no supported LookAndFeel with 
     *             the given name exists.
     */
    public static void setLookAndFeel(String name) {
        assert name != null : "null passed into setLookAndFeel(String)";
        LookAndFeelInfo info = Arrays.stream(UIManager.getInstalledLookAndFeels())
                .filter(i -> i.getName().equalsIgnoreCase(name))
                .findAny().orElseThrow(IllegalArgumentException::new);
        setLookAndFeelByClassName(info.getClassName());
    }
    
    /**
     * Sets the LookAndFeel to the LookAndFeel class with the given name.
     * @param className The name of the LookAndFeel class.
     * @throws          IllegalArgumentException - If the class name does not 
     *                  represent a supported LookAndFeel subclass.
     */
    public static void setLookAndFeelByClassName(String className) {
        assert className != null : "null passed into setLookAndFeelByClassName(String)";
        try {
            UIManager.setLookAndFeel(className);
        } catch (UnsupportedLookAndFeelException ex) {
            throw new IllegalArgumentException("Unsupported LookAndFeel: " + className, ex);
        }catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            throw new IllegalArgumentException("Failed to instantiate LookAndFeel: " + className, ex);
        }
    }
    
    /**
     * Sets the LookAndFeel to the platform default.
     * 
     * On Windows platforms, this sets the LookAndFeel to "windows". On other 
     * platforms, the default LookAndFeel is preserved.
     * 
     * @throws IllegalArgumentException - If setting the native LookAndFeel 
     *         failed for some reason.
     */
    public static void setNativeLookAndFeel() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (osName.contains("win")) {
            setLookAndFeel("windows");
        } else {
            String className = UIManager.getSystemLookAndFeelClassName();
            setLookAndFeelByClassName(className);
        }
    }
}
