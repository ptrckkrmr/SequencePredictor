package net.pgrid.sequencepredictor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit test for individual units of the Predictor class.
 * @author Patrick Kramer
 */
public class PredictorUnitTest {
    
    /**
     * Tests if the {@code fuzzyEq} method returns true when the values are 
     * equal enough.
     */
    @Test
    public void testFuzzyEqTrue() {
        assertTrue("1.0",     Predictor.fuzzyEq(1.0, 1.0));
        assertTrue("-1.0",    Predictor.fuzzyEq(-1.0, -1.0));
        assertTrue("2.25",    Predictor.fuzzyEq(2.25, 2.25));
        assertTrue("epsilon", Predictor.fuzzyEq(1 + (0.99 * Predictor.FUZZY_EQ_THRESHOLD), 1));
    }
    
    /**
     * Tests if the {@code fuzzyEq} method returns false when the values are too
     * different from each other.
     */
    @Test
    public void testFuzzyEqFalse() {
        assertFalse("1.0 - 2.0",  Predictor.fuzzyEq(1.0, 2.0));
        assertFalse("-1.0 - 1.0", Predictor.fuzzyEq(-1.0, 1.0));
        assertFalse("+/-epsilon", Predictor.fuzzyEq(-Predictor.FUZZY_EQ_THRESHOLD, Predictor.FUZZY_EQ_THRESHOLD));
        assertFalse("epsilon 0",  Predictor.fuzzyEq(0, Predictor.FUZZY_EQ_THRESHOLD));
    }
}
