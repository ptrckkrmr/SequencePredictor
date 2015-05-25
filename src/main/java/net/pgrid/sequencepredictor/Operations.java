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
import java.util.List;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

/**
 * Utility class that provides PredictOperation instances.
 * @author Patrick Kramer
 */
public class Operations {
    
    /**
     * Private constructor prevents instantiation.
     */
    private Operations() {
    }
    
    /**
     * Returns a List of all operations in this utility class.
     * @return 
     */
    public static List<PredictOperation> getAll() {
        return Arrays.asList(diff(), divide(), invert());
    }
    
    /**
     * Constructs a BinaryPredictOperation that takes the difference from all 
     * elements.
     * @return The BinaryPredictOperation
     */
    public static BinaryPredictOperation diff() {
        return create("a-b",
                      (a,b) -> b-a, 
                      (a,b) -> a+b);
    }
    
    /**
     * Constructs a BinaryPredictOperation that takes the multiplication factor 
     * from all elements.
     * @return The BinaryPredictOperation
     */
    public static BinaryPredictOperation divide() {
        return create("a/b",
                      (a,b) -> b/a, 
                      (a,b) -> a*b);
    }
    
    /**
     * Constructs an UnaryPredictOperation that takes the inverse from all 
     * elements.
     * 
     * For example, 0.5 becomes 2 and 8 becomes 0.125
     * 
     * @return The UnaryPredictOperation
     */
    public static UnaryPredictOperation invert() {
        return create("1/a",
                      d -> 1/d, 
                      d -> 1/d);
    }
    
    /**
     * Creates a BinaryPredictOperation from the given BinaryOperators.
     * @param desc    A description of the operator, for identification.
     * @param op      The operation itself
     * @param inverse The inverse of the operation, for constructing the next 
     *                element in the sequence.
     * @return        The created BinaryPredictOperation.
     */
    public static BinaryPredictOperation create(String desc, 
            DoubleBinaryOperator op, DoubleBinaryOperator inverse) {
        return new BinaryPredictOperation() {
            @Override public double applyAsDouble(double left, double right) {
                return op.applyAsDouble(left, right);
            }
            @Override public double applyInverse(double left, double right) {
                return inverse.applyAsDouble(left, right);
            }
            @Override public String description() {
                return desc;
            }
        };
    }
    
    /**
     * Creates a UnaryPredictOperation from the given UnaryOperators.
     * @param desc    A description of the operator, for identification.
     * @param op      The operation itself
     * @param inverse The inverse of the operation, for constructing the next 
     *                element in the sequence.
     * @return        The created UnaryPredictOperation.
     */
    public static UnaryPredictOperation create(String desc, 
            DoubleUnaryOperator op, DoubleUnaryOperator inverse) {
        return new UnaryPredictOperation() {
            @Override public double applyAsDouble(double d) {
                return op.applyAsDouble(d);
            }
            @Override public double applyInverse(double d) {
                return inverse.applyAsDouble(d);
            }
            @Override public String description() {
                return desc;
            }
            @Override public boolean canApplyAfter(PredictOperation op) {
                return !op.description().equals(desc);
            }
        };
    }
}
