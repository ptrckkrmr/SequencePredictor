package net.pgrid.sequencepredictor;

import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;

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
     * @param desc   A description for the test case.
     * @param values The values, not null.
     * @return       The input to the test case.
     */
    public static Object[] in(String desc, double... values) {
        double[] input  = new double[values.length - 1];
        double expected = values[values.length - 1];
        System.arraycopy(values, 0, input, 0, input.length);
        return new Object[] { input, expected, desc };
    }
    
    /**
     * The parameters for this parameterized unit test.
     * @return The parameters.
     */
    @Parameterized.Parameters(name = "{index} - {2}")
    public static List<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
            // polynomials
            in("constant",  4,4,4,                       4), // x[n] = 4
            in("linear (simple)",    1,2,3,4,            5), // x[n] = n
            in("linear",    2,4,6,8,                    10), // x[n] = 2n
            in("incremental", 1,2,4,7,                  11), // x[n] = x[n-1] + (x[n-1] - x[n-2])
            in("quadratic (simple)", 1,4,9,16,          25), // x[n] = n^2
            in("quadratic (translated)", 25,36,49,64,   81), // x[n] = (n+4)^2
            in("quadratic", 2,3,6,11,                   18), // x[n] = n^2 - 2n + 3
            in("linear (translated)", 0,1,2,             3), // x[n] = n - 1
            in("cubic", -6,0,20,66,150,                284), // x[n] = 2n^3 - 5n^2 + 7n - 10
            in("linear (negative)", 6,3,0,-3,           -6), // x[n] = -3n + 9
            
            // Alternating series
            in("alternating (0,1)", 0,1,0,1,            0), // x[n] = n % 2
            in("alternating (1,3,3,5,5)", 1,3,3,5,5,    7), // x[n] = n + (n % 2)
            in("alternating (0,1,0,-1)", 0,1,0,-1,0,1,  0), // x[n] = ... (?)
            
            // Exponential series
            in("exponential", 2,4,8,                16), // x[n] = 2^n
            in("exponential (negative)", -3,9,-27,  81), // x[n] = (-3)^n
            
            // Combinations
            in("exponential (translated)", -296,-284,-236,-44, 724), // x[n] = (2^n)^2 - 300
            
            // Fractions
            in("fractions (simple)", 1/1d, 1/2d, 1/3d,          1/4d), // x[n] =  1/n
            in("fractions", 1/2d, 2/3d, 3/4d, 4/5d, 5/6d, 6/7d, 7/8d), // x[n] = n / (n+1)
        });
    }
    
    @Parameter(0) public double[] values;
    @Parameter(1) public double expected;
    @Parameter(2) public String description;
    
    /**
     * Runs the test.
     * @throws  NoPatternFoundException - Never
     */
    @Test
    public void runTest() throws NoPatternFoundException {
        Predictor pred = new Predictor(values).init();
        assertEquals(expected, pred.getNext(), COMPARE_ACCURACY);
    }
    
}
