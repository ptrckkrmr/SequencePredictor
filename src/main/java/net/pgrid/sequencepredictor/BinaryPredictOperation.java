package net.pgrid.sequencepredictor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.DoubleBinaryOperator;

/**
 * Binary operation for predictions.
 * @author Patrick Kramer
 */
public interface BinaryPredictOperation extends 
        DoubleBinaryOperator, BinaryOperator<Double>, PredictOperation {

    @Override
    public default List<Double> apply(List<Double> list) {
        List<Double> result = new ArrayList<>(list.size()-1);
        for (int x = 1; x < list.size(); x++) {
            result.add(apply(list.get(x-1), list.get(x)));
        }
        return result;
    }

    @Override
    public default Double computeNext(List<Double> original, List<Double> computed) {
        Double lastOriginal = original.get(original.size()-1);
        Double lastComputed = computed.get(computed.size()-1);
        return applyInverse(lastOriginal, lastComputed);
    }
    
    @Override
    public double applyAsDouble(double left, double right);
    
    public double applyInverse(double left, double right);
    
    @Override
    public default Double apply(Double left, Double right) {
        return applyAsDouble(left, right);
    }
    
}
