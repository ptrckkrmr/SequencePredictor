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
