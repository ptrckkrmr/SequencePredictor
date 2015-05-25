package net.pgrid.sequencepredictor;

import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Unary operation for Predictions.
 * @author Patrick Kramer
 */
public interface UnaryPredictOperation extends 
        DoubleUnaryOperator, UnaryOperator<Double>, PredictOperation {
    
    /**
     * Applies this UnaryPredictOperation to every element in the given List.
     * @param input The input List
     * @return      The output List
     */
    @Override
    public default List<Double> apply(List<Double> input) {
        return input.stream()
                .map(this::apply)
                .collect(Collectors.toList());
    }

    /**
     * Computes the next value in sequence.
     * 
     * This method calls {@code applyInverse} on the last value in the 
     * {@code computed} List.
     * 
     * @param original The original List
     * @param computed The computed List
     * @return         The next value in sequence
     */
    @Override
    public default Double computeNext(List<Double> original,
            List<Double> computed) {
        assert !computed.isEmpty() : "Empty computed List";
        return applyInverse(computed.get(computed.size()-1));
    }
    
    /**
     * Applies the unary transformation to the given Double.
     * @param d The input double
     * @return  The result double
     */
    @Override
    public double applyAsDouble(double d);
    
    /**
     * Applies the inverse of this unary transformation to the given Double.
     * 
     * For any UnaryPredictOperation and a double d, 
     * {@code applyInverse(apply(d)) == d} should be true.
     * 
     * @param d The input double
     * @return  The result double
     */
    public double applyInverse(double d);
    
    /**
     * Calls {@code applyAsDouble} on the boxed value, and boxes the result.
     * @param d The input Double
     * @return  The result Double
     */
    @Override
    public default Double apply(Double d) {
        return applyAsDouble(d);
    }
}
