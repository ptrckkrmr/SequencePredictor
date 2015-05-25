package net.pgrid.sequencepredictor;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Predicts the next number in a certain (given) sequence.
 * 
 * The prediction is done using iterated differences, which can properly detect
 * and predict any polynomial sequence (given there are enough starting values 
 * given).
 * 
 * Additionally, the concept has been extended to also consider iterated 
 * multiplications (following the same principle) and the Predictor will go 
 * over possible combinations of difference and multiplication to be able to 
 * find and correctly extend a wide range of patterns.
 * 
 * @author Patrick Kramer
 */
public class Predictor {
    
    public static final int STRINGBUILDER_CAPACITY = 50;
    /**
     * The threshold for fuzzy equality testing of doubles.
     * 
     * This value is chosen so, that rounding errors can never cause the 
     * {@code fuzzyEq} method to return an incorrect value.
     */
    public static final double FUZZY_EQ_THRESHOLD  = 1E-14;
    
    // Explicitly defined as LinkedList to allow O(1) tail access.
    private final LinkedList<Double> values;
    private PredictOperation op;
    private Predictor diffs = null;
    
    public Predictor(double... values) {
        this.values = new LinkedList<>();
        Arrays.stream(values)
              .mapToObj(Double::valueOf)
              .forEach(this.values::add);
    }
    public Predictor(List<Double> ints) {
        this.values = new LinkedList<>(ints);
    }

    public void setOperation(PredictOperation op) {
        this.op = op;
    }
    
    /**
     * Initializes the Predictor.
     * 
     * Initializing this instance is required exactly once during the lifetime
     * of the Predictor instance, and before any other method is called on this 
     * Object. 
     * 
     * This step may take up to O(n^2) steps for complex sequences. 
     * 
     * @return Itself for fluency
     * @throws NoPatternFoundException - If no pattern can be found in the input.
     */
    public Predictor init() throws NoPatternFoundException {
        return init(null);
    }
    
    public Predictor init(PredictOperation prev) throws NoPatternFoundException {
        if (values.size() < 2) {
            throw new NoPatternFoundException("Not enough values to find pattern");
        }
        if (isAllEqual(values)) {
            return this;
        }
        
        for (PredictOperation operation : Operations.getAll()) {
            if (prev == null || operation.canApplyAfter(prev)) {
                try {
                    diffs = new Predictor(operation.apply(values)).init(operation);
                    op = operation;
                    System.out.println(">  Using " + operation.description());
                    return this;
                } catch (NoPatternFoundException ex) {
                    // Ignored on purpose
                    System.out.println(">  " + operation.description() + 
                            " fails: " + ex.getMessage());
                }
            }
        }
        throw new NoPatternFoundException("No Pattern can be found");
    }
    
    public List<Double> getComputed() {
        return Collections.unmodifiableList(values);
    }
    
    /**
     * Returns if this Predictor contains a sequence that is constant.
     * @return True if the sequence is constant, false otherwise
     */
    public boolean isConstant() {
        return diffs == null;
    }
    
    public Predictor getDiffs() {
        return diffs;
    }
    
    public double getNext() {
        double next = computeNext();
        values.add(next);
        return next;
    }
    
    protected double computeNext() {
        if (isConstant()) {
            return values.getLast();
        } else {
            diffs.getNext();
            return op.computeNext(values, diffs.getComputed());
        }
    }
    
    /**
     * Returns a symbolic representation of the Pattern found by this Predictor.
     * @return 
     */
    public String getPattern() {
        return getPattern(new StringBuilder(STRINGBUILDER_CAPACITY)).toString();
    }
    public StringBuilder getPattern(StringBuilder b) {
        if (isConstant()) {
            b.append("()");
        } else {
            b.append('(').append(op.description()).append(')').append(" -> ");
            diffs.getPattern(b);
        }
        return b;
    }
    
    /**
     * Returns an infinite Stream over predictions of this Predictor.
     * 
     * First use {@code limit(long)} on the returned Stream before doing any 
     * terminal operations. Otherwise it may cause an infinite loop, due to the 
     * Stream being infinite.
     * 
     * @return The Stream
     */
    public DoubleStream stream() {
        Iterator<Double> it = new PredictingIterator();
        Stream<Double> stream = StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(it, 
                        Spliterator.IMMUTABLE | Spliterator.NONNULL), false);
        return stream.mapToDouble(Double::doubleValue);
    }
    
    /**
     * Returns whether all doubles in the List are equal.
     * @param input A List of doubles.
     * @return      True if all elements are finite and equal, false otherwise
     * @throws      NoPatternFoundException - If non-finite numbers are present 
     *              in the List
     */
    public static boolean isAllEqual(List<Double> input) throws NoPatternFoundException {
        assert input != null;
        if (input.isEmpty()) {
            // Note that this branch never gets to be executed under normal 
            // circumstances. It just prevents `input.get(0)` from going wrong
            // when passing an empty List.
            return true;
        }
        double expected = input.get(0);
        if (input.stream().anyMatch(i -> !Double.isFinite(i))) {
            throw new NoPatternFoundException("Found non-real number in sequence");
        }
        return input.stream()
                .allMatch(i -> fuzzyEq(i, expected));
    }
    
    /**
     * Compares if the difference between the two doubles is less than the 
     * threshold as given by {@code FUZZY_EQ_THRESHOLD}.
     * 
     * The comparison is normalized to account for inaccuracies for large double
     * values.
     * 
     * @param a The first double
     * @param b The second double
     * @return  True if they are (approximately) equal, false otherwise
     */
    public static boolean fuzzyEq(double a, double b) {
        return Math.abs(a - b) <= Math.max(a, b) * FUZZY_EQ_THRESHOLD;
    }
    
    private class PredictingIterator implements Iterator<Double> {
        @Override
        public boolean hasNext() {
            return true;
        }
        @Override
        public Double next() {
            return getNext();
        }
    }
    
    public static void main(String[] args) {
        // Polynomials
        // Note: a nth order function needs at least n+2 elements to be conclusive!
        print("x^2",                    1,  4,  9, 16);
        print("x^2 - 2x + 3",           2,  3,  6, 11);
        print("x - 1",                  0,  1,  2    );
        print("2x^3 - 5x^2 + 7x - 10", -6,  0, 20, 66, 150);
        print("-3x + 12",               9,  6,  3);
        
        // Alternating series
        print("x % 2",                  0,  1,  0,  1);
        print("x + (x % 2)",            1,  3,  3,  5,  5);
    }
    
    public static void print(String fun, double... input) {
        assert input.length < 20 : "input too long";
        System.out.println("### " + fun);
        try {
            Predictor pred = new Predictor(input).init();
            System.out.println("> " + pred.getPattern());
            Arrays.stream(input)
                  .forEach(i -> System.out.print(i + "\t"));
            System.out.print('>');
            pred.stream()
                  .limit(20 - input.length)
                  .forEach(i -> System.out.print(i + "\t"));
            System.out.println();
            System.out.println();
        } catch (NoPatternFoundException ex) {
            System.out.println(ex.getClass().getSimpleName() + ": " + ex.getMessage());
        }
    }
}
