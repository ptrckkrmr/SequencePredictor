/* 
 *  Copyright (C) 2015, Patrick Kramer
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
    
    /**
     * Capacity of this {@code Predictor}'s internal StringBuilder.
     * 
     * The StringBuilder is used to construct String representations of the 
     * detected pattern for debugging or analysis.
     */
    public static final int STRINGBUILDER_CAPACITY = 50;
    
    /**
     * The threshold for fuzzy equality testing of doubles.
     * 
     * This value is chosen so, that rounding errors can never cause the 
     * {@code fuzzyEq} method to return an incorrect value.
     */
    public static final double FUZZY_EQ_THRESHOLD  = 1E-10;
    
    // Explicitly defined as LinkedList to allow O(1) tail access.
    private final LinkedList<Double> values;
    private PredictOperation op;
    private Predictor diffs = null;
    
    /**
     * Initializes a new Predictor using the given array of doubles.
     * @param values - The values to use for this Predictor, not null.
     */
    public Predictor(double... values) {
        this.values = new LinkedList<>();
        Arrays.stream(values).forEach(this.values::add);
    }
    
    /**
     * Initializes a new Predictor using the given List of doubles.
     * @param values - The values to use for this Predictor, not null.
     */
    public Predictor(List<Double> values) {
        this.values = new LinkedList<>(values);
    }

    /**
     * Sets the operation to use for this Predictor. 
     * 
     * This method is purely intended for constructing sequences. Calling this
     * method after detecting a sequence (using {@code init()}) may cause the 
     * detected pattern to be overridden.
     * @param op The PredictOperation, not null.
     */
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
    
    /**
     * Initializes this Predictor using the given previous operation.
     * @param prev The previous operation, can be null.
     * @return     This Predictor after determining the pattern.
     * @throws     NoPatternFoundException - If no pattern is found in the 
     *             values of this Predictor.
     */
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
                    List<Double> newValues = operation.apply(values);
                    diffs = new Predictor(newValues).init(operation);
                    op = operation;
                    return this;
                } catch (NoPatternFoundException ex) {
                    // Ignored on purpose
                }
            }
        }
        throw new NoPatternFoundException("No Pattern can be found");
    }
    
    /**
     * An unmodifiable view of the values in this Predictor.
     * @return The values in this Predictor as a read-only List.
     */
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
    
    /**
     * Returns the underlying Predictor that predicts the computed List of 
     * values.
     * @return The underlying Predictor. 
     */
    public Predictor getDiffs() {
        return diffs;
    }
    
    /**
     * Computes the next value and returns it.
     * 
     * Consecutive calls to this method will return new values in the sequence.
     * @return The next value.
     */
    public double getNext() {
        double next = computeNext();
        values.add(next);
        return next;
    }
    
    /**
     * Computes the next value and returns it.
     * 
     * Consecutive calls to this method will return the same value.
     * @return The next value.
     */
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
     * 
     * The pattern consists of the atomic operations this Predictor performs.
     * @return The pattern as a String.
     */
    public String getPattern() {
        return getPattern(new StringBuilder(STRINGBUILDER_CAPACITY)).toString();
    }
    
    /**
     * Adds a symbolic representation of the Pattern found by this Predictor 
     * to the given StringBuilder.
     * @param b The StringBuilder to append the Pattern to.
     * @return  The argument StringBuilder.
     */
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
        return input.stream().allMatch(i -> fuzzyEq(i, expected));
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
        return Math.abs(a - b) <= Math.abs(Math.max(a, b)) * FUZZY_EQ_THRESHOLD;
    }
    
    /**
     * Infinite Iterator class used to construct an infinite Stream.
     * 
     * @see Predictor.stream()
     */
    private class PredictingIterator implements Iterator<Double> {
        /**
         * Returns if this Iterator has a next value.
         * 
         * Because this Iterator is infinite, this method always returns true.
         * @return true.
         */
        @Override
        public boolean hasNext() {
            return true;
        }
        
        /**
         * Returns the next value in the infinite sequence.
         * @return The next value.
         */
        @Override
        public Double next() {
            return getNext();
        }
    }
}
