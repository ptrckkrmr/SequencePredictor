package net.pgrid.sequencepredictor;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit test for individual units of the Predictor class.
 * @author Patrick Kramer
 */
public class PredictorUnitTest {
    
    @Test
    public void testFuzzyEqTrue() {
        assertTrue("1.0",  Predictor.fuzzyEq(1.0, 1.0));
        assertTrue("-1.0", Predictor.fuzzyEq(-1.0, -1.0));
        assertTrue("2.25", Predictor.fuzzyEq(2.25, 2.25));
    }
}
