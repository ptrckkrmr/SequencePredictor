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

import java.util.List;

/**
 * Operation used as a computing step for predictions.
 * 
 * PredictOperations must always be reversible.
 * @author Patrick Kramer
 */
public interface PredictOperation {
    
    /**
     * Applies this operation to the input List.
     * @param input The input List
     * @return      The output List
     */
    public List<Double> apply(List<Double> input);
    
    /**
     * Applies the inverse of this PredictOperation to predict the next value.
     * 
     * This method takes two Lists: an {@code original} List and a 
     * {@code computed} List. The {@code original} is the List of original 
     * values as passed into the {@code apply} method of this PredictOperation.
     * 
     * The {@code computed} List is the List of values as given by the output of 
     * the {@code apply} method, extended by a single extra value. 
     * 
     * For example, a simple difference PredictOperation could be given the 
     * sequence {@code 1, 2, 3}. The apply method would produce the List
     * of differences: {@code 1, 1}. Some other operation would extend this 
     * List of differences to {@code 1, 1, 1}, and then the {@code computeNext}
     * method would use the extra 1 to compute the next value: {@code 3 + 1 = 4}. 
     * So the result of this method in that case is 4 (which is a logical 
     * continuation of the original sequence {@code 1, 2, 3}.
     * 
     * @param original The list of original values.
     * @param computed The list of computed values.
     * @return         The predicted next value in the {@code original} sequence
     */
    public Double computeNext(List<Double> original, List<Double> computed);
    
    /**
     * Returns a description of the operation that was applied.
     * @return The description of this operation.
     */
    public String description();
    
    public default boolean canApplyAfter(PredictOperation op) {
        return true;
    }
}
