package net.pgrid.sequencepredictor;

import java.util.Arrays;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Unit test for the Predictor class.
 * @author Patrick Kramer
 */
@RunWith(Parameterized.class)
public class PredictorTest {
    
    public static final double COMPARE_ACCURACY = 1E-10;
    
    /**
     * Syntactic sugar for mapping a varargs array to a normal array.
     * 
     * This method takes the values array and takes the last value as the expected 
     * value and the other values as input values. It returns an Object[] that 
     * represents the input to a single test case.
     * 
     * @param values The values
     * @return       The input to the test case.
     */
    public static Object[] in(double... values) {
        double[] input  = new double[values.length - 1];
        double expected = values[values.length - 1];
        System.arraycopy(values, 0, input, 0, input.length);
        return new Object[] { input, expected };
    }
    
    @Parameterized.Parameters
    public static List<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
            // polynomials
            in(4,4,4,                4), // x[n] = 4
            in(1,2,3,4,              5), // x[n] = n
            in(2,4,6,8,             10), // x[n] = 2n
            in(1,2,4,7,             11), // x[n] = x[n-1] + (x[n-1] - x[n-2])
            in(1,4,9,16,            25), // x[n] = n^2
            in(25,36,49,64,         81), // x[n] = (n+4)^2
            in(2,3,6,11,            18), // x[n] = n^2 - 2n + 3
            in(0,1,2,                3), // x[n] = n - 1
            in(-6,0,20,66,150,     284), // x[n] = 2n^3 - 5n^2 + 7n - 10
            in(6,3,0,               -3), // x[n] = -3n + 9
            
            // Alternating series
            in(0,1,0,1,              0), // x[n] = n % 2
            in(1,3,3,5,5,            7), // x[n] = n + (n % 2)
            in(0,1,0,-1,0,1,         0), // x[n] = ... (?)
            
            // Exponential series
            in(2,4,8,               16), // x[n] = 2^n
            in(-3,9,-27,            81), // x[n] = (-3)^n
            
            // Combinations
            in(-296,-284,-236,-44, 724), // x[n] = (2^n)^2 - 300
            
            // Fractions
            in(1/1d, 1/2d, 1/3d, 1/4d), // x[n] =  1/n
            in(1/2d, 2/3d, 3/4d, 4/5d, 5/6d, 6/7d)  // x[n] = n / (n+1)
        });
    }
    
    private final double expected;
    private final double[] values;
    
    /**
     * Creates a new PredictorTest instance for testing.
     * @param input    The input
     * @param expected The expected output
     */
    public PredictorTest(double[] input, double expected) {
        this.expected = expected;
        this.values   = input;
    }
    
    /**
     * Runs the test.
     * @throws  NoPatternFoundException - Never
     */
    @Test
    public void runTest() throws NoPatternFoundException {
        Predictor pred = new Predictor(values).init();
        System.out.println(pred.getPattern());
        assertEquals(expected, pred.getNext(), COMPARE_ACCURACY);
    }
    
}
