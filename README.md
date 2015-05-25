SequencePredictor
=================

This project provides a means to detect patterns in number sequences and predict
consecutive numbers in these sequences. The predictor can predict a large range
of patterns, including (but not limited to): all polynomial functions,
exponential functions and alternating series. In short, virtually any sequence
that can be expressed as a simple function can be computed. For example: the
function `f(n) = n / (n+1)` can be predicted, but sequences that depend on
previous values like the Fibonacci sequence cannot be predicted using the method
used in this Predictor.

Note that, in order to be able to predict the sequence, the Predictor must be
given sufficient input values. If there are not enough input values to determine
a conclusive pattern, the Predictor will fail. For example, the sequence `1, 4`
will be inconclusive because it can correspond to both `f(n) = 3n + 1` and
`f(n) = n^2`. In general (for polynomials), the Predictor needs n+2 values to
conclusively find a pattern for a nth order function.


How to use the SequencePredictor
--------------------------------

Currently, the SequencePredictor is not stand-alone. The only way to retrieve
predictions for a certain sequence is through code. The following statement
creates a Predictor object capable of making predictions (where `values` is a
`List<Double>` or double varargs array):

```java
Predictor predictor = new Predictor(values).init();
```

The `init()` method will return the Predictor for the sequence, or throw
a `NoPatternFoundException` when no pattern is found. To get the next value in
the sequence, use the `getNext()` method for a single value or the `stream()`
method to get an infinite Java 8 Stream over the predictions:

```java
// Returns the next value:
double nextValue = predictor.getNext();
// Returns a List with the next 10 values:
List<Double> nextValues = predictor.stream()
        .limit(10)
        .collect(Collectors.toList());
```

Note the use of `limit` on the returned Stream. Without it, the `collect` method
would take infinitly long because the Stream is infinite. It is recommended to
always call `limit` on the returned Stream.
