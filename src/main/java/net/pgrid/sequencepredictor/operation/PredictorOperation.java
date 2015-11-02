package net.pgrid.sequencepredictor.operation;

import java.util.List;
import net.pgrid.sequencepredictor.NoPatternFoundException;

/**
 * Represents a predict operation 
 * @author Patrick Kramer
 */
@FunctionalInterface
public interface PredictorOperation {

    /**
     * Tries to predict the next value in the sequence.
     * @param  input
     * @param  previous
     * @param  next 
     * @return 
     * @throws NoPatternFoundException
     */
    public double predict(List<Double> input, PredictorOperation previous, PredictorOperation next) 
            throws NoPatternFoundException;
    
    /**
     * Returns whether this operation can be applied to the input, given the last
     * operation that was applied before.
     * <p>
     * The default behavior returns true if (and only if) the input sequence is
     * not empty.
     * </p>
     * @param  input    The input sequence, not null.
     * @param  previous The previous operation, or null if no previous operation exists.
     * @return True if this PredictorOperation can be applied to the input,
     *         false otherwise.
     */
    public default boolean canApply(List<Double> input, PredictorOperation previous) {
        return !input.isEmpty();
    }
}
