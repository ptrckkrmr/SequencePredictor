package net.pgrid.sequencepredictor.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.pgrid.sequencepredictor.NoPatternFoundException;

/**
 * Predicts the next value in sequence using a set of possible operations.
 * @author Patrick Kramer
 */
public class Predictor implements PredictorOperation {
    
    private final List<PredictorOperation> operations;
    
    private PredictorOperation match = null;
    
    /**
     * Initializes a new instance of the {@link Predictor} class using the 
     * provided {@link PredictorOperation}s.
     * @param operations The {@link PredictorOperation} objects to use.
     */
    public Predictor(Collection<PredictorOperation> operations) {
        this.operations = new ArrayList<>(operations);
    }
    
    public List<PredictorOperation> getOperations() {
        return Collections.unmodifiableList(operations);
    }

    public PredictorOperation getMatch() {
        return match;
    }
    
    protected void setMatch(PredictorOperation newMatch) {
        this.match = newMatch;
    }
    
    /**
     * Predicts the next value in the sequence 
     * @param input
     * @return
     * @throws NoPatternFoundException 
     */
    public double predict(List<Double> input) throws NoPatternFoundException {
        return predict(input, null, new Predictor(getOperations()));
    }
    
    @Override
    public double predict(List<Double> input, PredictorOperation previous, PredictorOperation next)
            throws NoPatternFoundException {
        NoPatternFoundException exception = new NoPatternFoundException("No Pattern found");
        
        for (PredictorOperation operation : getOperations()) {
            if (operation.canApply(input, previous)) {
                try {
                    double result = operation.predict(input, previous, next);
                    setMatch(operation);
                    return result;
                } catch (NoPatternFoundException ex) {
                    exception.addSuppressed(ex);
                }
            }
        }
        
        // No pattern is found: throw the exception (with all suppressed 
        // exceptions to indicate failure).
        throw exception;
    }
}
